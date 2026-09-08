# Chaini AI Android — Final Launch-Safe Build

- Native AdMob rewarded test ads are wired to the WebView.
- Two native banner test slots are shown on Home only.
- WebView requests rewarded ads through `AndroidAds.showRewardedAd(...)`.
- Reward is reported back only after the native AdMob reward callback.
- If the user closes the rewarded ad early or it fails, no reward is granted.
- The current Web URL is `https://chaini-ai-app-free-test-safe-v11.vercel.app`.
- The web launch flow intentionally generates **zero AI images** until production funding is ready.
