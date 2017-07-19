# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

Major changes and highlights are in **bold**. Other changes that impact users are in plain. Changes impacting only developers are in *italics*.

## [Unreleased]
### Added
- **New OpenBST menu with animations and stuff!**
- **New BSP format that allows you to pack a whole story, including resources, in a single file. An assistant is available in the new menu.**
- **Now using HTML5 for viewing nodes! Which means that now your BST stories finally look good!**
- **A new module (HTB) is also available to configure the HTML5 environment**
- **New XBF module. You can now use multiple BST files in your resources and call their nodes.**
- **Automatic ID attribution is now available, simply use `*` as your node ID**
- **Node aliasing added, you can now give your nodes names and call them using their names!**
- You can choose between two fonts for your story now with the tag "fonts": either "libre_baskerville" (default) which is a serif, more classical font, or "ubuntu" which provides a more modern look.
- UI theme selector added
- File logging. Logs are now saved in ~/.openbst/logs
- We now have an About dialog! Gotta give credit :)
- Also introduce a small i18n completeness checker. It basically allows you to check if your current language is completely translated or not, and tells you what still needs to be translated.
- *BSTClient.getResourceHandler() method provides a unified way to get resource folder names*

### Changed
- **Switched from System Look and Feel to a custom Look and Feel that provides a unified look from platform to platform (and eases testing)**
- **BRM Loading has been made automatic. You do not need to call brm_load anymore.**
- **All node types can now be tagged.** Previously, only text nodes could receive tags, and this was unnoticed due to the fact no tags were useful for other nodes.
- Error page is now fully HTML and also much more swag
- *BRM Loading mechanism is much more flexible now (e.g InputStream loading)*
- *All modules were adapted to this new loading mechanism to add compatibility with BRM. This may break APIs.*
- *MAJOR API BREAK : Almost everything now has additional context requirements due to the addition of XBF, which now means you can have multiple BranchingStories for one file.*
- *MAJOR API BREAK : Changed package organisation because that's what we're doing now apparently*
- *All the icons are now final, and loaded differently.*

### Deprecated
- Everything related to the old brm_load method that is now unnecessary has been deprecated.

### Removed

### Fixed
- OpenBST will not crash anymore when loading a valid BST file with an unvalid extension.
- Sometimes, ambient sounds in SSB would not be recorded properly due to some race condition. This is now fixed.
- Fixed crash on stopping when there are no ambient sounds.
- Fixed some issues related to threading and concurrency (a few deadlocks and wrong thread errors here and there)
- *We're attempting to avoid System.out.println at all cost to avoid polluting System.out, please use the logger if you need to log something!*
- *Using Findbugs, a LOT of work has been done to stabilize OpenBST and make it more reliable, along with a log of fixes coming along.*
- *Also, the new Look and Feel crashes when changing UI components outside of the EDT thread, which is great since it allows us to track down what is still bugged and outside of the EDT thread.*

[Unreleased]: https://github.com/utybo/BST/compare/v1.1...dev
