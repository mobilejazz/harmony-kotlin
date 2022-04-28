//
//  HackerPostsViewState.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SampleCore

class HackerPostsViewState: ObservableObject, HackerPostsPresenterView {
    lazy var presenter = AppProvider.instance.shared.presenterComponent.getHackerPostsPresenter(view: self)

    @Published var items: [HackerPostViewModel] = []
    @Published var showLoader = false
    @Published var errorViewModel: ErrorViewModel?

    init() {
        presenter.onViewLoaded()
    }

    func onDisplayHackerPostList(hackerNewsPosts: [HackerNewsPost]) {
        showLoader = false
        items = hackerNewsPosts.map { hackerNewsPost in
            HackerPostViewModel(id: hackerNewsPost.id,
                                title: hackerNewsPost.title ?? "",
                                date: hackerNewsPost.time.toString())
        }
    }

    func onDisplayLoading() {
        showLoader = true
    }

    func onFailedWithFullScreenError(t: KotlinThrowable, retryBlock: @escaping () -> Void) {
        errorViewModel = ErrorViewModel(message: "There was an error loading", action: retryBlock)
    }
}
