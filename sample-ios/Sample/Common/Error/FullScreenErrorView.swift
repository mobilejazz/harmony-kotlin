//
//  FullScreenErrorView.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct ErrorViewModel: Identifiable {
    var id: String {
        message
    }

    let message: String
    let action: (() -> Void)?
}

struct FullScreenErrorView: View {
    let viewModel: ErrorViewModel

    var body: some View {
        VStack {
            Spacer()
            Text(viewModel.message).padding(.vertical, 16.0)
                .multilineTextAlignment(.center)
            if let action = viewModel.action {
                Button("Retry") {
                    action()
                }
            }
            Spacer()
        }.padding(.horizontal, 16.0)
    }
}

#if canImport(SwiftUI) && DEBUG

struct FullScreenErrorView_Previews: PreviewProvider {
    static let errorString =
        """
        Error. Preview Error Multiline. Error. Preview Error Multiline.
        """
    static var previews: some View {
        FullScreenErrorView(viewModel: ErrorViewModel(message: errorString,
                                                      action: nil))
        FullScreenErrorView(viewModel: ErrorViewModel(message: errorString, action: {
            print("It's a preview")
        }))
    }
}

#endif
