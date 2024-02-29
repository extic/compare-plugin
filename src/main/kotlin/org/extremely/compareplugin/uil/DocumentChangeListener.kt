package org.extremely.compareplugin.uil

import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class DocumentChangeListener(private var handler: () -> Unit) : DocumentListener {
    override fun insertUpdate(e: DocumentEvent?) {
        handler()
    }

    override fun removeUpdate(e: DocumentEvent?) {
        handler()
    }

    override fun changedUpdate(e: DocumentEvent?) {
        handler()
    }
}

fun JTextField.addChangeListener(handler: () -> Unit) {
    this.document.addDocumentListener(DocumentChangeListener(handler))
}