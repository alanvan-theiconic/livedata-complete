package com.alanvan.livedatacomplete

import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.psi.psiUtil.allChildren

class LiveDataPostfixTemplate : StringBasedPostfixTemplate(
    "l",
    "private val _someLiveData: MutableLiveData<Boolean> = MutableLiveData()" +
            "\nval someLiveData: LiveData<Boolean>" +
            "\n  get() = _someLiveData",
    LiveDataPostfixTemplateExpressionSelector()
) {
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
