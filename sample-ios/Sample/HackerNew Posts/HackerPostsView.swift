import SampleCore
import SwiftUI

struct HackerPostsView: View {
    @StateObject var viewState: HackerPostsViewState

    var body: some View {
        // Creates NavigationController structure
        NavigationView {
            if let errorViewModel = viewState.errorViewModel {
                FullScreenErrorView(viewModel: errorViewModel)

            } else {
                ScrollView(.vertical, showsIndicators: false) {
                    hackerPostList()
                }
                .clipped()
                .navigationTitle("HackerNews Posts")
            }
        }
        .overlay(LoadingOverlayView(showLoader: $viewState.showLoader))
    }

    private func hackerPostList() -> some View {
        return LazyVStack(alignment: .leading, spacing: 24.0) {
            // Iterates and listen for changes
            ForEach(viewState.items) { hackerPost in
                // Creates a link
                NavigationLink {
                    HackerPostDetailView(viewState: HackerPostDetailViewState(hackerNewsPostId: Int32(hackerPost.id)))
                        .navigationBarTitleDisplayMode(.inline)
                } label: {
                    VStack(alignment: .leading, spacing: 8.0) {
                        Text(hackerPost.date)
                            .font(.caption)
                            .foregroundColor(Color.theme.primary)
                        Text(hackerPost.title)
                            .font(.title2)
                            .foregroundColor(Color.theme.secondary)
                    }
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 16.0)
        .padding(.horizontal, 16.0)
    }

    private func hackerPostView(viewModel: HackerPostViewModel) -> some View {
        return VStack {
            Text(viewModel.title)
            Text(viewModel.date)
        }
    }
}

struct HackerPostsView_Previews: PreviewProvider {
    static var previews: some View {
        HackerPostsView(viewState: HackerPostsViewState())
    }
}
