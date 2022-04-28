//
//  HackerPostDetailView.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct HackerPostDetailView: View {
    @StateObject var viewState: HackerPostDetailViewState

    var body: some View {
        if let errorViewModel = viewState.errorViewModel {
            FullScreenErrorView(viewModel: errorViewModel)
        } else if let item = viewState.item {
            ScrollView(.vertical, showsIndicators: false) {
                VStack {
                    Text(item.date)
                        .foregroundColor(Color.theme.primary)
                        .font(.body)
                        .padding(.vertical, 8.0)
                    Text(item.title)
                        .foregroundColor(Color.theme.secondary)
                        .font(.title)
                        .padding(.bottom, 8.0)
                    Text(item.author)
                        .foregroundColor(Color.theme.primary)
                        .font(.body)
                        .padding(.bottom, 8.0)
                    Text(item.text)
                    Spacer()
                }
                .padding(.horizontal, 16.0)
            }
            .clipped()
        }
    }
}

struct HackerPostDetailView_Previews: PreviewProvider {
    static var previews: some View {
        HackerPostDetailView(viewState: HackerPostDetailViewState(hackerNewsPostId: 23))
    }
}
