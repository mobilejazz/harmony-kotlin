//
//  HackerPostDetailView.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import SampleCore

struct HackerPostDetailView: View {
    
    @StateObject  var viewModel: ObservableViewModel<HackerPostDetailViewState, HackerPostDetailAction, HackerPostDetailViewModel>
    
    var body: some View {
        switch viewModel.viewState {
        case is HackerPostDetailViewState.Loading:
            LoadingOverlayView()
        case let content as HackerPostDetailViewState.Content:
            hackerPostDetail(post: content.post)
        case let error as HackerPostDetailViewState.Error:
            FullScreenErrorView(viewModel: ErrorViewModel(message: error.message, action: {
                viewModel.onAction(action: HackerPostDetailAction.Refresh())
            }))
        default:
            unknownViewStateError()
        }
    }
    
    private func hackerPostDetail(post: HackerNewsPost) -> some View {
        ScrollView(.vertical, showsIndicators: false) {
            VStack {
                Text(post.time.toString())
                    .foregroundColor(Color.theme.primary)
                    .font(.body)
                    .padding(.vertical, 8.0)
                Text(post.title)
                    .foregroundColor(Color.theme.secondary)
                    .font(.title)
                    .padding(.bottom, 8.0)
                Text(post.by)
                    .foregroundColor(Color.theme.primary)
                    .font(.body)
                    .padding(.bottom, 8.0)
                Text(bodyText)
                Spacer()
            }
            .padding(.horizontal, 16.0)
        }
        .clipped()
    }
        
}
