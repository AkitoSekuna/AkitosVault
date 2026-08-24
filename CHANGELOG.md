# AkitosVault Changelog

## 21.2.0

* Fixed a compile-breaking regression: `AkitosEconomyProvider.depositPlayer()` was calling `IEconomyAPI.give()` as if it still returned a result object, after AkitosCore changed that method to `void`. AkitosVault could not be built from source at all in this state.
* Restored bank support end to end: AkitosCore's `BankManager` was implemented but never instantiated at runtime, and `ICoreAPI` had no way to reach it. Both fixed on the Core side, `AkitosEconomyProvider`'s 9 bank-related calls (`createBank`, `deleteBank`, `bankBalance`, `bankHas`, `bankWithdraw`, `bankDeposit`, `isBankOwner`, `isBankMember`, `getBanks`) now resolve and work correctly.
* No version bump was given for this fix (Vault's numbering doesn't follow the same per-release convention the message-modernization plugins use), but the jar you get from this build is functionally the first working one since the regression was introduced.
* `paper-api` already pinned to `1.21.11-R0.1-SNAPSHOT`, no change needed.
* Confirmed via a real `mvn clean install` (AkitosCore) then `mvn clean package` (AkitosVault), both `BUILD SUCCESS`.

## Earlier versions

Not yet documented. This changelog starts at the version where the compile break was found and fixed.
