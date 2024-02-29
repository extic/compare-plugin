package org.extremely.compareplugin.dialog

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import icons.CompareIcons
import net.miginfocom.swing.MigLayout
import org.extremely.compareplugin.uil.addChangeListener
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.*
import kotlin.io.path.exists
import kotlin.io.path.isDirectory


class CompareDialogWrapper : DialogWrapper(true) {
    private val leftPaneTextField = JTextField()
    private val rightPaneTextField = JTextField()
    private val errorField = JLabel()

    init {
        title = "Compare Files/Folders"
        init()

        okAction
    }

    private inner class MyCustomAction : AbstractAction("Compare", CompareIcons.compareIcon) {
        init {
            this.putValue("DefaultAction", true)
        }

        override fun actionPerformed(e: ActionEvent?) {
            doOKAction()
        }
    }

    override fun createDefaultActions() {
        super.createDefaultActions()
        this.myOKAction = MyCustomAction()
    }

    override fun createCenterPanel(): JComponent {
        PropertiesComponent.getInstance().run {
            leftPaneTextField.text = this.getValue("org.extremely.compare-plugin.comparisonLeftPane")
            rightPaneTextField.text = this.getValue("org.extremely.compare-plugin.comparisonRightPane")
        }

        leftPaneTextField.addChangeListener { validateDialog() }
        rightPaneTextField.addChangeListener { validateDialog() }

        val openLeftPaneAction = object: AbstractAction("", CompareIcons.leftFolderIcon) {
            override fun actionPerformed(e: ActionEvent) {
                choosePath(leftPaneTextField, rightPaneTextField)
            }
        }

        val openRightPaneAction = object: AbstractAction("", CompareIcons.rightFolderIcon) {
            override fun actionPerformed(e: ActionEvent) {
                choosePath(rightPaneTextField, leftPaneTextField)
            }
        }

        val switchAction = object : AbstractAction("", CompareIcons.switchIcon) {
            override fun actionPerformed(e: ActionEvent?) {
                val temp = rightPaneTextField.text
                rightPaneTextField.text = leftPaneTextField.text
                leftPaneTextField.text = temp
            }
        }

        errorField.text = " "
        errorField.foreground = JBColor.RED

        val dialogPanel = JPanel(MigLayout("", "10[]10[grow]10[]10", "10[]10[]10[]10[]"))
        dialogPanel.add(JLabel("Left Pane"), "w 100px")
        dialogPanel.add(leftPaneTextField, "grow x")
        dialogPanel.add(JButton(openLeftPaneAction), "wmax 50, wrap")
        dialogPanel.add(JButton(switchAction), "skip, alignx center, wmax 50, wrap")
        dialogPanel.add(JLabel("Right Pane"))
        dialogPanel.add(rightPaneTextField, "grow x")
        dialogPanel.add(JButton(openRightPaneAction), "wmax 50, wrap")
        dialogPanel.add(errorField, "span 3")

        dialogPanel.preferredSize = Dimension(500, 100)

        return dialogPanel
    }

    fun getLeftPath() : Path = Paths.get(leftPaneTextField.text)
    fun getRightPath() : Path = Paths.get(rightPaneTextField.text)

    private fun choosePath(primaryTextField: JTextField, secondaryTextField: JTextField) {
        val fileChooserDescriptor = FileChooserDescriptor(true, true, false, false, false, true)
        FileChooser.chooseFiles(fileChooserDescriptor, null, null, null) { chosenFile: List<VirtualFile> ->
            primaryTextField.text = chosenFile[0].path
            if (chosenFile.size > 1) {
                secondaryTextField.text = chosenFile[1].path
            }
        }
    }

    private fun validateDialog() {
        errorField.text = ""
        this.okAction.isEnabled = false

        PropertiesComponent.getInstance().run {
            this.setValue("org.extremely.compare-plugin.comparisonLeftPane", leftPaneTextField.text)
            this.setValue("org.extremely.compare-plugin.comparisonRightPane", rightPaneTextField.text)
        }

        if (leftPaneTextField.text.isNullOrEmpty() && rightPaneTextField.text.isNullOrEmpty()) {
            return
        }

        val leftPath = Paths.get(leftPaneTextField.text)
        if (!leftPath.exists()) {
            showError("Left path is not valid")
            return
        }

        val rightPath = Paths.get(rightPaneTextField.text)
        if (!rightPath.exists()) {
            showError("Right path is not valid")
            return
        }

        if (leftPaneTextField.text.isNullOrEmpty() || rightPaneTextField.text.isNullOrEmpty()) {
            return
        }

        if ((leftPath.isDirectory() && rightPath.isDirectory()) || (!leftPath.isDirectory() && !rightPath.isDirectory())) {
            this.okAction.isEnabled = true
            return
        }

        showError("Left and right panes should be both folders or both files")
    }

    private fun showError(text: String) {
        errorField.text = "ERROR: $text"
    }
}