package com.alanvan.livedatacomplete

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelector
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class LiveDataPostfixTemplateExpressionSelector : PostfixTemplateExpressionSelector {
    override fun hasExpression(context: PsiElement, copyDocument: Document, newOffset: Int): Boolean {
        return context.getParentOfType<KtTypeReference>(true)?.text?.matches(
            "LiveData<.*>".toRegex()
        ) ?: false
    }

    override fun getExpressions(context: PsiElement, document: Document, offset: Int): MutableList<PsiElement> {
        return context.getParentOfType<KtTypeReference>(true)?.let {
            mutableListOf(it)
        } ?: mutableListOf()
    }

    override fun getRenderer(): Function<PsiElement, String> {
        return Function { element -> element.text }
    }
}