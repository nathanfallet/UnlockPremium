/*
*  Copyright (C) 2022 Groupe MINASTE
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
*/

import SwiftUI

public struct UnlockPremiumView: View {
    
    @Binding var isPresented: Bool
    
    @StateObject var viewModel: UnlockPremiumViewModel
    
    /// Create the unlock premium view
    /// - Parameters:
    ///   - configuration: The configuration for the view
    ///   - isPresented: A binding to control if the view is presented or not
    public init(configuration: UnlockPremiumConfig, isPresented: Binding<Bool>) {
        self._viewModel = StateObject(wrappedValue: UnlockPremiumViewModel(configuration: configuration))
        self._isPresented = isPresented
    }
    
    public var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 32) {
                    ForEach(viewModel.configuration.arguments, id: \.title) { arg in
                        HStack(alignment: .top, spacing: 16) {
                            Image(systemName: arg.icon)
                                .font(.title2)
                                .foregroundColor(.accentColor)
                                .frame(width: 35)
                            VStack(alignment: .leading, spacing: 8) {
                                Text(arg.title)
                                    .font(.title2)
                                    .foregroundColor(.accentColor)
                                Text(arg.description)
                            }
                            Spacer()
                        }
                    }
                    if viewModel.product != nil {
                        if viewModel.transactionPending {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle())
                        } else {
                            Button(action: viewModel.buyPremium) {
                                Text(LocalizedStringKey("premium_unlock"), bundle: .module)
                            }
                            .foregroundColor(.white)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.accentColor)
                            .cornerRadius(8)
                            Button(
                                action: {
                                    if viewModel.configuration.introMode {
                                        isPresented = false
                                    } else {
                                        viewModel.restorePremium()
                                    }
                                },
                                label: {
                                    Text(LocalizedStringKey(
                                        viewModel.configuration.introMode ? "premium_no_thanks" : "premium_restore"
                                    ), bundle: .module)
                                }
                            )
                        }
                    } else {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle())
                            .onAppear(perform: viewModel.fetchProduct)
                    }
                }
                .padding()
            }
            .navigationTitle(Text(LocalizedStringKey(
                viewModel.configuration.introMode ? "premium_unlock_intro" : "premium_unlock"
            ), bundle: .module))
            .toolbar {
                ToolbarItemGroup(placement: .cancellationAction) {
                    Button(
                        action: {
                            isPresented = false
                        },
                        label: {
                            Text(LocalizedStringKey("cancel"), bundle: .module)
                        }
                    )
                }
            }
            .onAppear(perform: viewModel.onAppear)
            .alert(isPresented: $viewModel.paymentFailed) {
                Alert(
                    title: Text(LocalizedStringKey("error"), bundle: .module),
                    message: nil,
                    dismissButton: .default(Text(LocalizedStringKey("ok"), bundle: .module))
                )
            }
            .onChange(of: viewModel.paymentSucceeded) { _ in
                if viewModel.paymentSucceeded {
                    isPresented = false
                }
            }
        }
    }
    
}
