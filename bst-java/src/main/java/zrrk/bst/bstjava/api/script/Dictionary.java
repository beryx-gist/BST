/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.atteo.classindex.ClassIndex;

import zrrk.bst.bstjava.api.BSTClient;
import zrrk.bst.bstjava.api.BSTException;

/**
 * This class serves as an index dictionary for all the existing actions and
 * checkers and their implementation. It detects them using the class-index
 * library, which creates lists of of implementations of {@link ScriptAction}
 * and {@link ScriptChecker} at compile time.
 *
 * @author utybo
 *
 */
public class Dictionary
{
    /**
     * A HashMap with action names as keys and script actions as values
     */
    private final HashMap<String, ScriptAction> actions = new HashMap<>();
    /**
     * A HashMap with checker names as keys and script checkers as values
     */
    private final HashMap<String, ScriptChecker> checkers = new HashMap<>();

    private final HashMap<String, ExtNNDFactory> nndFactories = new HashMap<>();

    /**
     * Creates a simple dictionary using all the implementations known.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Dictionary() throws InstantiationException, IllegalAccessException
    {
        for(final Class<? extends ScriptAction> jclass : ClassIndex
                .getSubclasses(ScriptAction.class))
        {
            ScriptAction sa;
            try
            {
                sa = jclass.getConstructor().newInstance();
                final String[] names = sa.getName();
                if(names != null)
                {
                    for(final String s : names)
                    {
                        actions.put(s, sa);
                    }
                }
            }
            catch(IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e)
            {
                e.printStackTrace();
            }

        }
        for(final Class<? extends ScriptChecker> jclass : ClassIndex
                .getSubclasses(ScriptChecker.class))
        {
            try
            {
                final ScriptChecker sa = jclass.getConstructor().newInstance();
                final String[] names = sa.getName();
                if(names != null)
                {
                    for(final String s : names)
                    {
                        checkers.put(s, sa);
                    }
                }
            }
            catch(IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e)
            {
                e.printStackTrace();
            }

        }
        for(final Class<? extends ExtNNDFactory> jclass : ClassIndex
                .getSubclasses(ExtNNDFactory.class))
        {
            try
            {
                final ExtNNDFactory factory = jclass.getConstructor().newInstance();
                final String[] names = factory.getNames();
                if(names != null)
                {
                    for(final String s : names)
                    {
                        nndFactories.put(s, factory);
                    }
                }
            }
            catch(IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                    | SecurityException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return The implementation of {@link ScriptAction} which works for the
     *         given action
     * @param action
     *            The action we are looking for
     */
    public ScriptAction getAction(final String action) throws BSTException
    {
        return actions.get(action);
    }

    /**
     * @return The implementation of {@link ScriptChecker} which works for the
     *         given checker
     * @param action
     *            The checker we are looking for
     */
    public ScriptChecker getChecker(final String checker) throws BSTException
    {
        return checkers.get(checker);
    }

    public NextNodeDefiner getExtNND(String group, String group2, BSTClient client, int line,
            String sourceName) throws BSTException
    {
        if(nndFactories.get(group) == null)
        {
            throw new BSTException(line, "Unknown external next node definer : " + group,
                    sourceName);
        }
        return nndFactories.get(group).createNND(group, group2, line, client);
    }
}
