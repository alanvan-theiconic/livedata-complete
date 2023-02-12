package com.alanvan.livedatacomplete

import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.idea.formatter.commitAndUnblockDocument
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType

class LiveDataPostfixTemplate : StringBasedPostfixTemplate(
    "l",
    "private val _someLiveData: MutableLiveData<Boolean> = MutableLiveData()" +
            "\nval someLiveData: LiveData<Boolean>" +
            "\n  get() = _someLiveData",
    LiveDataPostfixTemplateExpressionSelector()
) {
    companion object {
        const val LIVEDATA_FQ_NAME = "androidx.lifecycle.LiveData"
        const val MUTABLE_LIVEDATA_FQ_NAME = "androidx.lifecycle.MutableLiveData"
    }
    override fun expandForChooseExpression(expr: PsiElement, editor: Editor) {
        (expr.containingFile as? KtFile)?.let { file ->
            file.commitAndUnblockDocument()
            file.childrenOfType<KtImportList>().firstOrNull()?.let { importList ->
                importList.addImport(LIVEDATA_FQ_NAME)
                importList.addImport(MUTABLE_LIVEDATA_FQ_NAME)
            }
            file.commitAndUnblockDocument()
        }
        super.expandForChooseExpression(expr, editor)
    }

    private fun KtImportList.addImport(fqNameString: String) {
        val importedFqNames = imports.mapNotNull { it.importedFqName }
        val importFqName = FqName(fqNameString)
        if (!importedFqNames.contains(importFqName)) {
            val ktPsiFactory = KtPsiFactory(project, false)
            val importDirective = ktPsiFactory.createImportDirective(
                ImportPath(importFqName, false)
            )
            add(importDirective)
            CodeStyleManager.getInstance(project).reformat(this)
        }
    }

    override fun getTemplateString(element: PsiElement): String {
        var variableName = ""
        var isVariableNameAssigned = false
        element.parent.allChildren.iterator().forEach {
            if (!isVariableNameAssigned && it.elementType?.debugName == "IDENTIFIER") {
                variableName = it.text
                isVariableNameAssigned = true
            }
        }
        val variableType = "<.*>".toRegex().find(element.text)?.value?.removeSurrounding("<", ">")

        return "private val _$variableName: MutableLiveData<$variableType> = MutableLiveData()" +
                "\nval $variableName: LiveData<$variableType>" +
                "\n  get() = _$variableName" +
                "\n\$END$"
    }
}
