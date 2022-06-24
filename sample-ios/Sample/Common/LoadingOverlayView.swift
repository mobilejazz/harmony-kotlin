//
//  LoadingOverlayView.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

import SwiftUI

struct LoadingOverlayView: View {

    var body: some View {
        ZStack {
            Color(white: 1)
            ActivityIndicator(color: Color.theme.primary)
        }
    }
}
