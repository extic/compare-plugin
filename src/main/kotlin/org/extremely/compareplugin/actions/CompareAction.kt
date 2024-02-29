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
import com.intellij.util.io.isDirectory
import org.extremely.compareplugin.dialog.CompareDialogWrapper
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.Icon

class CompareAction : AnAction {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    constructor() : super()

    constructor(text: String?, description: String?, icon: Icon?) : super(text, description, icon)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!

        val compareDialogWrapper = CompareDialogWrapper()
        if (compareDialogWrapper.showAndGet()) {
            if (compareDialogWrapper.getLeftPath().isDirectory()) {
                compareDirectories(project, compareDialogWrapper.getLeftPath(), compareDialogWrapper.getRightPath())
            } else {
                compareFiles(project, compareDialogWrapper.getLeftPath(), compareDialogWrapper.getRightPath())
            }
        }
    }

    private fun compareDirectories(project: Project, leftPath: Path, rightPath: Path) {
        val leftElement = getDirDiffElementFromPath(leftPath, project)
        val rightElement = getDirDiffElementFromPath(rightPath, project)

        val dirDiffSettings = buildDirDiffSettingsFromApplicationSettings();
        DirDiffManager.getInstance(project).showDiff(leftElement, rightElement, dirDiffSettings)
    }

    private fun compareFiles(project: Project, leftPath: Path, rightPath: Path) {
        val leftContent: DocumentContent = DiffContentFactory.getInstance().create(Files.readString(leftPath))
        val rightContent: DocumentContent = DiffContentFactory.getInstance().create(Files.readString(rightPath));
        val request = SimpleDiffRequest("File Comparison", leftContent, rightContent, leftPath.toFile().absolutePath, rightPath.toFile().absolutePath)
        DiffManager.getInstance().showDiff(project, request)
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