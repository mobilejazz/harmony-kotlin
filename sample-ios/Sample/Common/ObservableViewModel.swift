import SampleCore
import SwiftUI

/// Wrapper around KMM ViewModel to make it observable by SwiftUI
class ObservableViewModel<
    VS: ViewState,
    A: Action,
    VM: ViewModel<VS, A>
>: ObservableObject {
    
    /// Observable state of the view
    @Published private(set) var viewState: VS
    
    private let viewModel: VM
    
    init(viewModel: VM) {
        self.viewModel = viewModel
        self.viewState = viewModel.viewState.value as! VS
        
        viewModel.observe(
            viewModel.viewState,
            onChange: { state in
                self.viewState = state as! VS
            })
    }
    
    /// Notifies the ViewModel of an Action that occurred on the View
    func onAction(action: A) {
        viewModel.onAction(action: action)
    }
    
}

/// Throws  a fatal error  when the state is unknown
func unknownViewStateError() -> Never {
    fatalError("Unknown ViewState")
}
