//
//  ObservableNavigation.swift
//  Sample
//
//  Created by Fran Montiel on 28/7/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SampleCore

/// Wrapper around KMM Navigation to make it observable by SwiftUI
class ObservableNavigation<N: Navigation>: ObservableObject {
    
    @Published var navigation: N? = nil
    
    /// Collects the navigation from the event and set it to be emitted
    func set(event: OneShotEvent<N>) {
        event.consume {
            if let navigation = $0 {
                self.navigation = navigation
            }
        }
    }
    
    /// Sets the navigation to be emitted
    func set(navigation: N) {
        self.navigation = navigation
    }
}

