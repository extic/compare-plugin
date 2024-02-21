package org.extremely.compareplugin.actions

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.contents.DocumentContent
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.ide.diff.DiffElement
import com.intellij.ide.diff.DiffErrorElement
import com.intellij.ide.diff.DirDiffSettings
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diff.DirDiffManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.Icon

class CompareAction : AnAction {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    /**
     * This default constructor is used by the IntelliJ Platform framework to instantiate this class based on plugin.xml
     * declarations. Only needed in [CompareAction] class because a second constructor is overridden.
     */
    constructor() : super()

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     *
     * @param text        The text to be displayed as a menu item.
     * @param description The description of the menu item.
     * @param icon        The icon to be used with the menu item.
     */
    constructor(text: String?, description: String?, icon: Icon?) : super(text, description, icon)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!

        val content1: DocumentContent = DiffContentFactory.getInstance().create("aaa barak levinsonasd")
        val content2: DocumentContent = DiffContentFactory.getInstance().create("fasf barak asfasfafs levinson");
        val request = SimpleDiffRequest("Window Title", content1, content2, "Title 1", "Title 2")
        DiffManager.getInstance().showDiff(project, request)


        val firstPath = Paths.get("C:\\work\\sy-main\\etc\\tests\\regression_bin\\external\\old")
        val secondPath = Paths.get("C:\\work\\sy-main\\etc\\tests\\regression_bin\\external\\new")

        val firstElement = getDirDiffElementFromPath(firstPath, project)
        val secondElement = getDirDiffElementFromPath(secondPath, project)

        val dirDiffSettings = buildDirDiffSettingsFromApplicationSettings();
        DirDiffManager.getInstance(project).showDiff(firstElement, secondElement, dirDiffSettings)
    }

    override fun update(e: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

    private fun buildDirDiffSettingsFromApplicationSettings(): DirDiffSettings {
        val dirDiffSettings = DirDiffSettings()

        dirDiffSettings.showNewOnTarget = true
        dirDiffSettings.showNewOnSource = true
        dirDiffSettings.showEqual = true
        dirDiffSettings.showDifferent = true

        return dirDiffSettings
    }

    private fun getDirDiffElementFromPath(path: Path, project: Project): DiffElement<*> {
        var firstElement: DiffElement<*>?
        val file: VirtualFile = getLocalFileSystem().findFileByIoFile(path.toFile())!!
        firstElement = DirDiffManager.getInstance(project).createDiffElement(file)
        if (firstElement == null) {
            firstElement = DiffErrorElement(path.toString(), path.toString())
        }
        return firstElement
    }

    private fun getLocalFileSystem(): LocalFileSystem {
        return VirtualFileManager
            .getInstance()
            .getFileSystem(LocalFileSystem.PROTOCOL) as LocalFileSystem
    }
}