import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        
        // Настройка статус-бара: светлый фон с темным содержимым
        controller.overrideUserInterfaceStyle = .light
        
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    init() {
        // Настройка внешнего вида статус-бара
        UIApplication.shared.statusBarStyle = .darkContent
    }
    
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
            .preferredColorScheme(.light)
    }
}



