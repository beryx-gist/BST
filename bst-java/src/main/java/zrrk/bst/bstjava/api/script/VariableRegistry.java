/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.bstjava.api.script;

import java.util.HashMap;

/**
 * A registry of all the variables. The contract is that there cannot be
 * multiple variables with the same name.
 *
 * @author utybo
 *
 */
public class VariableRegistry implements Cloneable
{
    private HashMap<String, Integer> variables = new HashMap<>();
    private HashMap<String, String> strVar = new HashMap<>();

    public void put(final String name, final int var)
    {
        remove(name);
        variables.put(name, var);
    }

    public void put(final String name, final String value)
    {
        remove(name);
        strVar.put(name, value);
    }

    public void remove(final String name)
    {
        variables.remove(name);
        strVar.remove(name);
    }

    @Deprecated
    public int getInt(final String name)
    {
        return variables.getOrDefault(name, 0);
    }

    public HashMap<String, Integer> getAllInt()
    {
        return variables;
    }

    public HashMap<String, String> getAllString()
    {
        return strVar;
    }

    public void reset()
    {
        variables.clear();
        strVar.clear();
    }

    public Class<?> typeOf(final String name)
    {
        if(variables.containsKey(name))
        {
            return Integer.class;
        }

        return null;
    }

    public Object get(final String varName, final Object ifNotFound)
    {
        Object tryingToFindMe = null;
        if(variables.containsKey(varName))
        {
            tryingToFindMe = variables.get(varName);
        }
        else if(strVar.containsKey(varName))
        {
            tryingToFindMe = strVar.get(varName);
        }
        else
        {
            tryingToFindMe = ifNotFound;
        }
        return tryingToFindMe;
    }

    @Override
    public VariableRegistry clone()
    {
        try
        {
            VariableRegistry vr = (VariableRegistry)super.clone();
            vr.strVar = new HashMap<>();
            vr.variables = new HashMap<>();
            vr.strVar.putAll(strVar);
            vr.variables.putAll(variables);
            return vr;
        }
        catch(CloneNotSupportedException e)
        {
            // Cannot happen
            e.printStackTrace();
            return null;
        }
    }

    public int getSize()
    {
        return variables.size() + strVar.size();
    }

    public String dump()
    {
        return "INTEGERS : " + variables.toString() + " | STRINGS : " + strVar.toString();
    }

    /**
     * Completely empty this registry. The contract is that #getSize() will
     * return 0 after calling this method.
     */
    public void clear()
    {
        variables.clear();
        strVar.clear();
    }

    /**
     * Merge this registry's content with the given one's content. This will
     * prioritize the new registry's values over this one's.
     * 
     * @param mergeWith
     */
    public void merge(VariableRegistry mergeWith)
    {
        variables.putAll(mergeWith.getAllInt());
        strVar.putAll(mergeWith.getAllString());
    }
}
