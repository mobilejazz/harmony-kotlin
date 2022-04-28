//
//  HackerPostDetailViewState.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SampleCore

class HackerPostDetailViewState: ObservableObject, HackerPostDetailPresenterView {
    lazy var presenter = AppProvider.instance.shared.presenterComponent.getHackerPostDetailPresenter(view: self)

    @Published var item: HackerPostDetailViewModel?
    @Published var showLoader = false
    @Published var errorViewModel: ErrorViewModel?

    let hackerNewsPostId: Int32

    init(hackerNewsPostId: Int32) {
        self.hackerNewsPostId = hackerNewsPostId
        presenter.onViewLoaded(hackerNewsPostId: hackerNewsPostId)
    }

    func onDisplayHackerPost(hackerNewsPost: HackerNewsPost) {
        showLoader = false

        item = HackerPostDetailViewModel(id: hackerNewsPost.id,
                                         date: hackerNewsPost.time.toString(),
                                         title: hackerNewsPost.title ?? "",
                                         author: "By \(hackerNewsPost.by)",
                                         text: bodyText)
    }

    func onDisplayLoading() {
        showLoader = true
    }

    func onFailedWithFullScreenError(t: KotlinThrowable, retryBlock: @escaping () -> Void) {
        errorViewModel = ErrorViewModel(message: "Error", action: retryBlock)
    }
}

struct HackerPostDetailViewModel: Identifiable {
    let id: Int64

    let date: String
    let title: String
    let author: String
    let text: String
}
