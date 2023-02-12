package com.alanvan.livedatacomplete

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import kotlin.jvm.internal.Intrinsics

class LiveDataPostfixTemplateProvider : PostfixTemplateProvider {
    private val _templates: MutableSet<PostfixTemplate> = mutableSetOf(
        LiveDataPostfixTemplate()
    )

    override fun getTemplates(): MutableSet<PostfixTemplate> {
        return _templates
    }

    override fun isTerminalSymbol(currentChar: Char): Boolean {
        return currentChar == '.' || currentChar == '!'
    }

    override fun afterExpand(file: PsiFile, editor: Editor) {
        Intrinsics.checkNotNullParameter(file, "file")
        Intrinsics.checkNotNullParameter(editor, "editor")
    }

    override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile {
        Intrinsics.checkNotNullParameter(copyFile, "copyFile")
        Intrinsics.checkNotNullParameter(realEditor, "realEditor")
        return copyFile
    }

    override fun preExpand(file: PsiFile, editor: Editor) {
        Intrinsics.checkNotNullParameter(file, "file")
        Intrinsics.checkNotNullParameter(editor, "editor")
    }
}