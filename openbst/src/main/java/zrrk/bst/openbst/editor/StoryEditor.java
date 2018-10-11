/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package zrrk.bst.openbst.editor;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.apache.commons.io.IOUtils;

import net.miginfocom.swing.MigLayout;
import zrrk.bst.bstjava.api.BSTException;
import zrrk.bst.bstjava.api.story.BranchingStory;
import zrrk.bst.openbst.Icons;
import zrrk.bst.openbst.Messagers;
import zrrk.bst.openbst.OpenBST;
import zrrk.bst.openbst.utils.Lang;

@SuppressWarnings("serial")
public class StoryEditor extends JPanel implements EditorControl<BranchingStory>
{
    private StoryDetailsEditor details;
    private StoryNodesEditor nodesEditor;
    private File lastFileLocation;
    private JTabbedPane tabbedPane;

    public StoryEditor(BranchingStory baseStory) throws BSTException
    {
        setLayout(new MigLayout("hidemode 3", "[grow]", "[][grow]"));

        JToolBar toolBar = new JToolBar();
        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        add(toolBar, "cell 0 0,growx");

        JButton btnSaveAs = new JButton(Lang.get("saveas"),
                new ImageIcon(Icons.getImage("Save As", 16)));
        btnSaveAs.addActionListener(e ->
        {
            saveAs();
        });
        toolBar.add(btnSaveAs);

        JButton btnSave = new JButton(Lang.get("save"), new ImageIcon(Icons.getImage("Save", 16)));
        btnSave.addActionListener(e ->
        {
            save();
        });
        toolBar.add(btnSave);

        JButton btnPlay = new JButton(Lang.get("play"),
                new ImageIcon(Icons.getImage("Circled Play", 16)));
        btnPlay.addActionListener(ev ->
        {
            play();
        });
        toolBar.add(btnPlay);

        JButton btnFilePreview = new JButton(Lang.get("editor.exportpreview"),
                new ImageIcon(Icons.getImage("PreviewText", 16)));
        btnFilePreview.addActionListener(e ->
        {
            try
            {
                String s = exportToString();
                JDialog dialog = new JDialog(OpenBST.getGUIInstance(),
                        Lang.get("editor.exportpreview"));
                JTextArea jta = new JTextArea(s);
                jta.setLineWrap(true);
                jta.setWrapStyleWord(true);
                dialog.add(new JScrollPane(jta));

                dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                dialog.setSize((int)(Icons.getScale() * 350), (int)(Icons.getScale() * 300));
                dialog.setLocationRelativeTo(OpenBST.getGUIInstance());
                dialog.setVisible(true);
            }
            catch(Exception x)
            {
                OpenBST.LOG.error("Failed to preview", x);
                Messagers.showException(OpenBST.getGUIInstance(), Lang.get("editor.previewerror"),
                        x);
            }
        });
        toolBar.add(btnFilePreview);

        Component horizontalGlue = Box.createHorizontalGlue();
        toolBar.add(horizontalGlue);

        JButton btnClose = new JButton(Lang.get("close"),
                new ImageIcon(Icons.getImage("Cancel", 16)));
        btnClose.addActionListener(e ->
        {
            askClose();
        });
        toolBar.add(btnClose);

        for(final Component component : toolBar.getComponents())
        {
            if(component instanceof JButton)
            {
                ((JButton)component).setHideActionText(false);
                ((JButton)component).setToolTipText(((JButton)component).getText());
                ((JButton)component).setText("");
            }
        }

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        add(tabbedPane, "cell 0 1,grow");

        tabbedPane.addTab("Beta Warning", new StoryEditorWelcomeScreen());

        details = new StoryDetailsEditor(this);
        tabbedPane.addTab(Lang.get("editor.details"), details);

        nodesEditor = new StoryNodesEditor();
        tabbedPane.addTab(Lang.get("editor.nodes"), nodesEditor);

        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("control S"), "doSave");
        this.getActionMap().put("doSave", new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });

        importFrom(baseStory);

        installKeyoardShortcuts();
    }

    private void installKeyoardShortcuts()
    {
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("control s"), "save");
        inputMap.put(KeyStroke.getKeyStroke("control shift s"), "saveAs");
        inputMap.put(KeyStroke.getKeyStroke("control p"), "play");
        inputMap.put(KeyStroke.getKeyStroke("control 1"), "showDetails");
        inputMap.put(KeyStroke.getKeyStroke("control 2"), "showNodes");

        actionMap.put("save", toAction(() -> save()));
        actionMap.put("saveAs", toAction(() -> saveAs()));
        actionMap.put("play", toAction(() -> play()));
        actionMap.put("showDetails", toAction(() -> tabbedPane.setSelectedIndex(1)));
        actionMap.put("showNodes", toAction(() -> tabbedPane.setSelectedIndex(2)));
    }

    private Action toAction(Runnable run)
    {
        return new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                run.run();
            }
        };
    }

    public boolean askClose()
    {
        int i = Messagers.showConfirm(OpenBST.getGUIInstance(), Lang.get("editor.asksave"),
                Messagers.OPTIONS_YES_NO_CANCEL, Messagers.TYPE_WARNING,
                Lang.get("editor.asksave.title"));
        if(i == Messagers.OPTION_YES)
        {
            if(save())
            {
                OpenBST.getGUIInstance().removeTab(this);
                return true;
            }
        }
        else if(i == Messagers.OPTION_NO)
        {
            OpenBST.getGUIInstance().removeTab(this);
            return true;
        }
        else
            return false;
        return false;
    }

    @Override
    public void importFrom(BranchingStory from) throws BSTException
    {
        details.importFrom(from.getTagMap());
        nodesEditor.importFrom(from.getAllNodes());
    }

    @Override
    public BranchingStory exportToObject()
    {
        return null;
    }

    @Override
    public String exportToString() throws BSTException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("# OpenBST Editor Export\n");
        sb.append(details.exportToString());
        sb.append("\n\n");
        sb.append(nodesEditor.exportToString());

        return sb.toString();
    }

    public boolean save()
    {
        if(lastFileLocation == null)
        {
            return saveAs();
        }
        else
        {
            try
            {
                return doSave(lastFileLocation);
            }
            catch(IOException | BSTException e1)
            {
                OpenBST.LOG.error("Failed saving a file", e1);
                Messagers.showException(OpenBST.getGUIInstance(), Lang.get("editor.savefail"), e1);
            }
        }
        return false;
    }

    private boolean doSave(File f) throws IOException, BSTException
    {
        try(FileOutputStream fos = new FileOutputStream(f))
        {
            IOUtils.write(exportToString(), fos, StandardCharsets.UTF_8);
            return true;
        }
    }

    public boolean saveAs()
    {
        FileDialog fd = new FileDialog(OpenBST.getGUIInstance(), Lang.get("editor.saveloc"),
                FileDialog.SAVE);
        fd.setLocationRelativeTo(OpenBST.getGUIInstance());
        fd.setVisible(true);
        if(fd.getFile() != null)
        {
            final File file = new File(
                    fd.getFile().endsWith(".bst") ? fd.getDirectory() + fd.getFile()
                            : fd.getDirectory() + fd.getFile() + ".bst");
            try
            {
                doSave(file);
                lastFileLocation = file;
                return true;
            }
            catch(IOException | BSTException e1)
            {
                OpenBST.LOG.error("Failed saving a file", e1);
                Messagers.showException(OpenBST.getGUIInstance(), Lang.get("editor.savefail"), e1);
            }
        }
        return false;
    }

    public void play()
    {
        try
        {
            String s = exportToString();
            File f = Files.createTempDirectory("openbst").toFile();
            File bstFile = new File(f, "exported.bst");
            try(FileOutputStream fos = new FileOutputStream(bstFile);)
            {
                IOUtils.write(s, fos, StandardCharsets.UTF_8);
            }
            OpenBST.getGUIInstance().openStory(bstFile);

        }
        catch(Exception e)
        {
            OpenBST.LOG.error("Export failed", e);
            Messagers.showException(OpenBST.getGUIInstance(), Lang.get("editor.exportfail"), e);
        }

    }

    public void updateTabTitle()
    {
        OpenBST.getGUIInstance().updateName(this, getTitle());
    }

    public String getTitle()
    {
        return Lang.get("editor.title").replace("$t", details.getTitle()).replace("$a",
                details.getAuthor());
    }
}
