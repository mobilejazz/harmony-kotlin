//
//  Color+Extensions.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

protocol IThemeColors {
    static var primary: Color { get }
    static var secondary: Color { get }
    static var text: Color { get }
}

private struct ThemeColors: IThemeColors {
    static let primary = Color("primary")
    static let secondary = Color("secondary")
    static let text: Color = Color("text")
}

extension Color {
    /// Theme Colors
    static var theme: IThemeColors.Type {
        return ThemeColors.self
    }
}
