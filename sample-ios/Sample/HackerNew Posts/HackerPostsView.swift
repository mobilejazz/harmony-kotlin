import SampleCore
import SwiftUI

struct HackerPostsView: View {
    
    @StateObject var viewModel = ObservableViewModel<HackerPostsViewState, HackerPostsAction, HackerPostsViewModel>(viewModel: AppProvider.instance.shared.viewModelComponent.getHackerPostsViewModel())
    @StateObject var observableNavigation = ObservableNavigation<HackerPostsNavigation>()
    
    var body: some View {
        NavigationView {
            switch viewModel.viewState {
            case is HackerPostsViewState.Loading:
                LoadingOverlayView()
            case let error as HackerPostsViewState.Error:
                FullScreenErrorView(viewModel: ErrorViewModel(message: error.message, action: nil))
            case let content as HackerPostsViewState.Content:
                hackerPostList(posts: content.posts, navigationEvent: content.navigation)
            default:
                unknownViewStateError()
            }
        }
    }
    
    private func hackerPostList(posts: [HackerNewsPost], navigationEvent: OneShotEvent<HackerPostsNavigation>) -> some View {
        observableNavigation.set(event: navigationEvent)
        
        return ScrollView(.vertical, showsIndicators: false){
            LazyVStack(alignment: .leading, spacing: 24.0) {
                // Iterates and listen for changes
                ForEach(posts) { hackerNewsPost in
                    VStack(alignment: .leading, spacing: 8.0) {
                        Text(hackerNewsPost.time.toString())
                            .font(.caption)
                            .foregroundColor(Color.theme.primary)
                        Text(hackerNewsPost.title)
                            .font(.title2)
                            .foregroundColor(Color.theme.secondary)
                    }.onTapGesture {
                        viewModel.onAction(action: HackerPostsAction.PostSelected(id: hackerNewsPost.id))
                    }
                    // Creates a link
                    NavigationLink(destination: HackerPostDetailView(viewModel:  ObservableViewModel<HackerPostDetailViewState, HackerPostDetailAction, HackerPostDetailViewModel>(viewModel: AppProvider.instance.shared.viewModelComponent.getHackerPostDetailViewModel(postId: hackerNewsPost.id))),
                                   tag: HackerPostsNavigation.ToDetail(id: hackerNewsPost.id),
                                   selection: $observableNavigation.navigation,
                                   label: { EmptyView() }).navigationBarTitleDisplayMode(.inline)
                }.buttonStyle(.plain)
            }
            .padding(.vertical, 16.0)
            .padding(.horizontal, 16.0)
        }
        .clipped()
        .navigationTitle("HackerNews Posts")
    }
}

extension HackerNewsPost: Identifiable {}
