package com.moban.flow.projects.highlight

import android.content.Context
import com.moban.mvp.BaseMvpPresenter


/**
 * Created by H. Len Vo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class HighlightProjectPresenterIml: BaseMvpPresenter<IHighlightProjectView>, IHighlightProjectPresenter {
    private var view: IHighlightProjectView? = null
    private var context: Context? = null

    override fun attachView(view: IHighlightProjectView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }
}
