//
//  ActivityIndicator.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct ActivityIndicator: View {
    let width: CGFloat
    let height: CGFloat
    let color: Color

    init(color: Color) {
        self.width = 60
        self.height = 60
        self.color = color
    }

    init(width: CGFloat, height: CGFloat, color: Color) {
        self.width = width
        self.height = height
        self.color = color
    }

    @State private var degress = 0.0
    @State private var trim = 0.3
    var body: some View {
        Circle()
            .trim(from: 0.0, to: trim)
            .stroke(color, lineWidth: 4.0)
            .frame(width: width, height: height)
            .rotationEffect(Angle(degrees: degress))
            .onAppear(perform: { self.start() })
    }

    private func start() {
        var multiplier = 1.0
        _ = Timer.scheduledTimer(withTimeInterval: 0.02, repeats: true) { _ in
            withAnimation {
                self.degress += 10.0
                self.trim += 0.01 * multiplier
            }

            if self.degress >= 360.0 {
                self.degress = 0.0
            }

            if self.trim >= 0.8 {
                multiplier = -1.0
            } else if self.trim <= 0 {
                multiplier = 1.0
            }
        }
    }
}

struct ActivityIndicator_Previews: PreviewProvider {
    static var previews: some View {
        ActivityIndicator(color: Color.theme.primary)
    }
}
