package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.lightningkite.rx.combineLatest
import com.lightningkite.rx.optional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

val PsiElement.module: Module?
    get() = ModuleUtilCore.findModuleForPsiElement(this)

val Project.psiTreeChangeObservable: Observable<PsiTreeChangeEvent>
    get() {
        return Observable.create {
            val listener = object : PsiTreeChangeListener {
                override fun beforeChildAddition(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun beforeChildMovement(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun beforePropertyChange(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun childAdded(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun childRemoved(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun childReplaced(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun childrenChanged(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun childMoved(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }

                override fun propertyChanged(event: PsiTreeChangeEvent) {
                    it.onNext(event)
                }
            }
            PsiManager.getInstance(this).addPsiTreeChangeListener(listener, this)
            it.setDisposable(Disposable.fromAction {
                PsiManager.getInstance(this).removePsiTreeChangeListener(listener)
            })
        }
    }

val Project.currentEditorObservable: Observable<Editor>
    get() {
        return Observable.create {
            val bus = messageBus.connect(this)
            val listener = object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    event.manager.selectedTextEditor?.let { e -> it.onNext(e) }
                }
            }
            bus.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)
            it.setDisposable(Disposable.fromAction {
                bus.disconnect()
            })
        }
    }

val Editor.caretObservable: Observable<CaretEvent>
    get() {
        return Observable.create {
            val listener = object : CaretListener {
                override fun caretAdded(event: CaretEvent) {
                    it.onNext(event)
                }

                override fun caretRemoved(event: CaretEvent) {
                    it.onNext(event)
                }

                override fun caretPositionChanged(event: CaretEvent) {
                    it.onNext(event)
                }
            }
            caretModel.addCaretListener(listener)
            it.setDisposable(Disposable.fromAction {
                caretModel.removeCaretListener(listener)
            })
        }
    }

val Project.psiElementAtCaret: Observable<Optional<PsiElement>>
    get() {
        return currentEditorObservable
            .switchMap { it.caretObservable.map { c -> it to c } }
            .map { (editor, caret) ->
                caret.caret?.offset?.let { offset ->
                    PsiDocumentManager.getInstance(this).getPsiFile(editor.document)?.findElementAt(offset)
                }.optional
            }
    }