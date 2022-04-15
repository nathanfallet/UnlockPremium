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

import StoreKit

class UnlockPremiumViewModel: NSObject, ObservableObject, SKProductsRequestDelegate, SKPaymentTransactionObserver {
    
    // Product
    @Published var product: SKProduct?
    
    // Status
    @Published var transactionPending = false
    @Published var productLoadFailed = false
    @Published var paymentFailed = false
    @Published var paymentSucceeded = false
    
    // Configuration
    let configuration: UnlockPremiumConfig
    
    // Payment queue
    let paymentQueue = SKPaymentQueue()
    
    // Strong reference to request
    var request: SKProductsRequest?
    
    // Initializer
    init(configuration: UnlockPremiumConfig) {
        self.configuration = configuration
        super.init()
        
        // Add the observer
        paymentQueue.add(self)
    }
    
    // Handle on appear
    func onAppear() {
        configuration.onAppear()
    }
    
    // Fetch product
    func fetchProduct() {
        // Create a request
        request = SKProductsRequest(productIdentifiers: Set([configuration.productIdentifier]))
        request?.delegate = self
        request?.start()
    }
    
    // Handle when product should be bought
    func buyPremium() {
        // Unwrap product
        guard let product = product else { return }
        
        // Mark as pending
        self.transactionPending = true
        
        // Create a payment
        let payment = SKPayment(product: product)
        
        // Add it to queue
        paymentQueue.add(payment)
    }
    
    func restorePremium() {
        // Mark as pending
        self.transactionPending = true
        
        // Launch restore
        paymentQueue.restoreCompletedTransactions()
    }
    
    // Handle response from product request
    func productsRequest(_ request: SKProductsRequest, didReceive response: SKProductsResponse) {
        DispatchQueue.main.async {
            // Save product
            self.product = response.products.first
        }
    }
    
    // Handle fail from product request
    func request(_ request: SKRequest, didFailWithError error: Error) {
        DispatchQueue.main.async {
            // Error
            self.productLoadFailed = true
        }
    }
    
    // Handle when transactions are updated
    func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
        // Iterate transaction
        for transaction in transactions {
            // Check for correct product id
            if transaction.payment.productIdentifier == product?.productIdentifier {
                // Check state
                if transaction.transactionState == .purchased || transaction.transactionState == .restored {
                    // Succeeded
                    self.configuration.completionHandler()
                    self.paymentSucceeded = true
                } else if transaction.transactionState == .failed {
                    // Failed
                    self.paymentFailed = true
                    self.transactionPending = false
                }
                
                // End transaction if needed
                if transaction.transactionState != .purchasing {
                    // Finish transaction
                    queue.finishTransaction(transaction)
                }
            }
        }
    }
    
}
