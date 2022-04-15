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

import Foundation

public struct UnlockPremiumConfig {
    
    /// Arguments shown in the unlock view
    public var arguments: [PremiumArgument]
    
    /// Product identifier of the premium purchase
    public var productIdentifier: String
    
    /// If the view should be shown in Intro mode
    public var introMode: Bool = false
    
    /// Custom action called when the view appears
    public var onAppear: () -> Void = {}
    
    /// Completion handler when the purchase successes
    public var completionHandler: () -> Void
    
}
