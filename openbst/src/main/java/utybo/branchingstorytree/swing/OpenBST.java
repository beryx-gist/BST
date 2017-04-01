/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.miginfocom.swing.MigLayout;
import utybo.branchingstorytree.api.BSTClient;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionnary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.JScrollablePanel.ScrollableSizeHint;

/**
 * OpenBST is an open source implementation of the BST language that aims to be
 * fully compatible with every single feature of BST.
 * <p>
 * This class is both the main class and the main JFrame.
 *
 * @author utybo
 *
 */
public class OpenBST extends JFrame
{
    /**
     * Version number of OpenBST
     */
    public static String version;
    private static final long serialVersionUID = 1L;

    public static final Logger LOG = LogManager.getLogger("OpenBST");
//    public static final Configuration CONFIG;

    static
    {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationFactory.newConfigurationBuilder();
        builder.setConfigurationName("OpenBST-default");
        builder.setStatusLevel(Level.INFO);
    }

    /**
     * The parser that will be reused throughout the entire session.
     */
    private final BranchingStoryTreeParser parser = new BranchingStoryTreeParser();

    /**
     * The JFrame instance
     */
    private static OpenBST instance;

    // --- IMAGES ---
    public static Image ideaImage;
    public static Image blogImage;
    public static Image controllerImage;
    public static Image inLoveImage;
    public static Image activeDirectoryImage;
    public static Image openFolderImage;
    public static Image cancelImage, errorImage, aboutImage, renameImage;
    public static Image addonSearchImage, addonSearchMediumImage, closeImage, closeBigImage, jumpImage, jumpBigImage, exportImage;
    public static Image gearsImage, importImage, invisibleImage, muteImage, pictureImage, refreshImage, refreshBigImage, returnImage;
    public static Image returnBigImage, saveAsImage, speakerImage, synchronizeImage, synchronizeBigImage, undoImage, undoBigImage, visibleImage;

    /**
     * Container for all the tabs
     */
    private final JTabbedPane container;

    /**
     * Launch OpenBST
     *
     * @param args
     *            Arguments. The first argument is the language code to be used
     */
    public static void main(final String[] args)
    {
        version = OpenBST.class.getPackage().getImplementationVersion();
        if(version == null)
        {
            version = "<unknown version>";
        }
        LOG.error("Error example", new RuntimeException("incredible"));
        LOG.info("OpenBST version " + version + ", part of the BST project");
        LOG.trace("[ INIT ]");

        LOG.trace("Loading language files");
        Lang.mute();
        loadLang(args.length > 0 ? args[0] : null);
        
        LOG.trace("Applying Look and Feel");
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            LOG.trace("=> GTKLookAndFeel");

            // If GTKLookAndFeel was successfully loaded, apply Gnome Shell fix
            try
            {
                final Toolkit xToolkit = Toolkit.getDefaultToolkit();
                final java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
                awtAppClassNameField.setAccessible(true);
                awtAppClassNameField.set(xToolkit, Lang.get("title"));
            }
            catch(final Exception e)
            {
                LOG.warn("Could not apply X fix", e);
            }

        }
        catch(final Exception e)
        {
            // Do not print as an exception is thrown in most cases
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                LOG.trace("=> System LookAndFeel");
            }
            catch(final Exception e1)
            {
                LOG.trace("=> No LookAndFeel compatible. Using default", e1);
            }
        }

        instance = new OpenBST();
    }

    /**
     * Open a prompt asking for a BST File
     *
     * @return The file selected, or null if none was chosen/the dialog was
     *         closed
     */
    private File askForFile()
    {
        final FileDialog jfc = new FileDialog(instance);
        jfc.setLocationRelativeTo(instance);
        jfc.setTitle(Lang.get("file.title"));
        jfc.setVisible(true);
        if(jfc.getFile() != null)
        {
            LOG.trace("=> File selected");
            final File file = new File(jfc.getDirectory() + jfc.getFile());
            LOG.debug("[ LAUNCHING ]");
            return file;
        }
        else
        {
            LOG.trace("=> No file selected.");
            return null;
        }
    }

    /**
     * Load and parse a file, using appropriate dialogs if an error occurs to
     * inform the user and even give him the option to reload the file
     *
     * @param file
     *            The file to load
     * @param client
     *            The BST Client. This is required for parsing the file
     * @return
     */
    public BranchingStory loadFile(final File file, final BSTClient client)
    {
        try
        {
            LOG.trace("Parsing story");
            return parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"))), new Dictionnary(), client);
        }
        catch(final IOException e)
        {
            LOG.error("=! IOException caught", e);
            JOptionPane.showMessageDialog(instance, Lang.get("file.error").replace("$e", e.getClass().getSimpleName()).replace("$m", e.getMessage()), Lang.get("error"), JOptionPane.ERROR_MESSAGE);
            return null;
        }
        catch(final BSTException e)
        {
            LOG.error("=! BSTException caught", e);
            String s = Lang.get("file.bsterror.1");
            s += Lang.get("file.bsterror.2");
            s += Lang.get("file.bsterror.3").replace("$l", "" + e.getWhere());
            if(e.getCause() != null)
            {
                s += Lang.get("file.bsterror.4").replace("$e", e.getCause().getClass().getSimpleName()).replace("$m", e.getCause().getMessage());
            }
            s += Lang.get("file.bsterror.5").replace("$m", e.getMessage());
            s += Lang.get("file.bsterror.6");
            if(JOptionPane.showConfirmDialog(instance, s, Lang.get("bsterror"), JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                LOG.debug("Reloading");
                return loadFile(file, client);
            }
            return null;
        }
        catch(final Exception e)
        {
            LOG.error("=! Random exception caught", e);
            JOptionPane.showMessageDialog(instance, Lang.get("file.crash"), Lang.get("error"), JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Load the default language (which should be English) as well as the user's
     * language.. We avoid loading all the langauge files to avoid having our
     * RAM usage blowing up
     *
     * @param userCustomLanguage
     *            The language to use in the application, which must be one
     *            defined in the langs.json file
     */
    private static void loadLang(final String userCustomLanguage)
    {
        final Map<String, String> languages = new Gson().fromJson(new InputStreamReader(OpenBST.class.getResourceAsStream("/utybo/branchingstorytree/swing/lang/langs.json")), new TypeToken<Map<String, String>>()
        {}.getType());
        try
        {
            Lang.loadTranslationsFromFile(Lang.getDefaultLanguage(), OpenBST.class.getResourceAsStream("/utybo/branchingstorytree/swing/lang/" + languages.get("default")));
        }
        catch(final Exception e)
        {
            LOG.warn("Exception while loading language file : " + languages.get("default"), e);
        }
        if(userCustomLanguage != null)
        {
            Lang.setSelectedLanguage(new Locale(userCustomLanguage));
        }
        final Locale userLanguage = Lang.getSelectedLanguage();
        languages.forEach((k, v) ->
        {
            if(userLanguage.equals(new Locale(k)) && !v.equals(languages.get("default")))
            {
                try
                {
                    Lang.loadTranslationsFromFile(userLanguage, OpenBST.class.getResourceAsStream("/utybo/branchingstorytree/swing/lang/" + v));
                }
                catch(final Exception e)
                {
                    LOG.warn("Exception while loading language file : " + v, e);
                }
            }
        });
    }

    /**
     * Add a story by creating a tab and initializing its panel. Also triggers
     * post-creation events (such as NSFW warnings)
     *
     * @param story
     *            The story to create a tab for
     * @param file
     *            The file the story was loaded from
     * @param client
     *            The client to use
     * @return
     */
    private StoryPanel addStory(final BranchingStory story, final File file, final TabClient client)
    {
        LOG.trace("Creating tab");
        final StoryPanel sp = new StoryPanel(story, this, file, client);
        container.addTab(sp.getTitle(), null, sp, null);
        container.setSelectedIndex(container.getTabCount() - 1);
        if(!sp.postCreation())
        {
            container.removeTabAt(container.getTabCount() - 1);
            return null;
        }
        else
        {
            return sp;
        }
    }

    /**
     * Load all the icons and initialize the frame
     */
    public OpenBST()
    {
        setTitle("OpenBST " + version);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try
        {
            LOG.trace("=> Loading icons");
            activeDirectoryImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Active Directory.png"));
            setIconImage(activeDirectoryImage);
            blogImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Blog.png"));
            controllerImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Controller.png"));
            ideaImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Idea.png"));
            inLoveImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/In Love.png"));
            openFolderImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Open Folder.png"));
            cancelImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Cancel.png"));
            errorImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Error.png"));
            aboutImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/About.png"));
            renameImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/Rename.png"));

            addonSearchImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Addon Search.png"));
            addonSearchMediumImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Addon Search Medium.png"));
            closeImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Close.png"));
            closeBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Close Big.png"));
            invisibleImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Invisible.png"));
            exportImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Export.png"));
            gearsImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Gears.png"));
            importImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Import.png"));
            jumpImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Jump.png"));
            jumpBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Jump Big.png"));
            muteImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Mute.png"));
            pictureImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Picture.png"));
            refreshImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Refresh.png"));
            refreshBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Refresh Big.png"));
            returnImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Return.png"));
            returnBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Return Big.png"));
            saveAsImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Save as.png"));
            speakerImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Speaker.png"));
            synchronizeImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Synchronize.png"));
            synchronizeBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Synchronize Big.png"));
            undoImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Undo.png"));
            undoBigImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Undo Big.png"));
            visibleImage = ImageIO.read(getClass().getResourceAsStream("/utybo/branchingstorytree/swing/icons/toolbar/Visible.png"));

            // Note : this does not work with GTKLookAndFeel
            UIManager.put("OptionPane.errorIcon", new ImageIcon(cancelImage));
            UIManager.put("OptionPane.informationIcon", new ImageIcon(aboutImage));
            UIManager.put("OptionPane.questionIcon", new ImageIcon(renameImage));
            UIManager.put("OptionPane.warningIcon", new ImageIcon(errorImage));
        }
        catch(final IOException e1)
        {
            LOG.warn("=! IOException caught when loading icon", e1);
        }
        getContentPane().setLayout(new BorderLayout());
        container = new JTabbedPane();
        getContentPane().add(container, BorderLayout.CENTER);

        final JScrollablePanel welcomePanel = new JScrollablePanel();
        welcomePanel.setScrollableHeight(ScrollableSizeHint.STRETCH);
        welcomePanel.setScrollableWidth(ScrollableSizeHint.FIT);
        welcomePanel.setLayout(new MigLayout("", "[grow,center]", "[][][][][][][][][]"));
        container.add(new JScrollPane(welcomePanel));
        container.setTitleAt(0, Lang.get("welcome"));

        final JLabel lblOpenbst = new JLabel("<html><font size=32>" + Lang.get("title"));
        lblOpenbst.setIcon(new ImageIcon(activeDirectoryImage));
        welcomePanel.add(lblOpenbst, "cell 0 0");

        final JLabel lblWelcomeToOpenbst = new JLabel(Lang.get("welcome.intro"));
        welcomePanel.add(lblWelcomeToOpenbst, "cell 0 1");

        final JButton btnOpenAFile = new JButton(Lang.get("welcome.open"));
        btnOpenAFile.setIcon(new ImageIcon(openFolderImage));
        btnOpenAFile.addActionListener(e ->
        {
            final File f = askForFile();
            if(f != null)
            {
                final TabClient client = new TabClient(instance);
                final BranchingStory bs = loadFile(f, client);
                if(bs != null)
                {
                    addStory(bs, f, client);
                }
            }
        });
        welcomePanel.add(btnOpenAFile, "cell 0 2");

        final JSeparator separator = new JSeparator();
        welcomePanel.add(separator, "cell 0 3,growx");

        final JLabel lblwhatIsBst = new JLabel(Lang.get("welcome.whatis"));
        lblwhatIsBst.setFont(lblwhatIsBst.getFont().deriveFont(28F));
        welcomePanel.add(lblwhatIsBst, "cell 0 4");

        final JLabel lblbstIsA = new JLabel(Lang.get("welcome.about"));
        welcomePanel.add(lblbstIsA, "cell 0 5,alignx center,growy");

        final JLabel lblimagineItcreate = new JLabel(Lang.get("welcome.imagine"));
        lblimagineItcreate.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblimagineItcreate.setHorizontalTextPosition(SwingConstants.CENTER);
        lblimagineItcreate.setIcon(new ImageIcon(ideaImage));
        welcomePanel.add(lblimagineItcreate, "flowx,cell 0 6,alignx center,aligny top");

        welcomePanel.add(Box.createHorizontalStrut(20), "cell 0 6");

        final JLabel lblwriteItwriteSaid = new JLabel(Lang.get("welcome.write"));
        lblwriteItwriteSaid.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblwriteItwriteSaid.setHorizontalTextPosition(SwingConstants.CENTER);
        lblwriteItwriteSaid.setIcon(new ImageIcon(blogImage));
        welcomePanel.add(lblwriteItwriteSaid, "cell 0 6,aligny top");

        welcomePanel.add(Box.createHorizontalStrut(20), "cell 0 6");

        final JLabel lblPlayIt = new JLabel(Lang.get("welcome.play"));
        lblPlayIt.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblPlayIt.setHorizontalTextPosition(SwingConstants.CENTER);
        lblPlayIt.setIcon(new ImageIcon(controllerImage));
        welcomePanel.add(lblPlayIt, "cell 0 6,aligny top");

        welcomePanel.add(Box.createHorizontalStrut(20), "cell 0 6");

        final JLabel lblEnjoyIt = new JLabel(Lang.get("welcome.enjoy"));
        lblEnjoyIt.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblEnjoyIt.setHorizontalTextPosition(SwingConstants.CENTER);
        lblEnjoyIt.setIcon(new ImageIcon(inLoveImage));
        welcomePanel.add(lblEnjoyIt, "cell 0 6, aligny top");

        final JLabel lblIconsByIconscom = new JLabel(Lang.get("welcome.icons"));
        lblIconsByIconscom.setEnabled(false);
        welcomePanel.add(lblIconsByIconscom, "cell 0 8,alignx left");

        setSize(830, 480);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Remove a story panel from the tabs
     *
     * @param storyPanel
     */
    public void removeStory(final StoryPanel storyPanel)
    {
        container.remove(storyPanel);
    }

}
