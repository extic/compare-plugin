package icons

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

object CompareIcons {

    @JvmField
    val compareIcon: Icon = getIcon("/icons/compare.svg", CompareIcons::class.java)

    val leftFolderIcon: Icon = getIcon("/icons/leftFolder.svg", CompareIcons::class.java)
    val rightFolderIcon: Icon = getIcon("/icons/rightFolder.svg", CompareIcons::class.java)
    val switchIcon: Icon = getIcon("/icons/switch.svg", CompareIcons::class.java)
}