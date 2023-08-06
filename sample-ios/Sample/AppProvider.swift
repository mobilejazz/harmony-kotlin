//
//  AppProvider.swift
//  Sample
//
//  Created by Javi on 27/4/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SampleCore

protocol DependencyInjection {
    var shared: ApplicationComponent { get }
}

class AppProvider: DependencyInjection {
    lazy var shared: ApplicationComponent = ApplicationDefaultModule(coreLogger: IOSConsoleLogger())
}

extension AppProvider {
    static let instance = AppProvider()
}
