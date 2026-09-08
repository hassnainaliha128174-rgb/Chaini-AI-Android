# Chaini AI APP — Play Store + Billing setup

## Current launch behavior
- Target/compile SDK: 36.
- Rewarded image flow: 2 rewarded ads unlock the generator; a third rewarded ad is shown when Generate Image is pressed; then the app displays a launch-testing message and does not generate an image.
- AI image generation is controlled server-side by `IMAGE_GENERATION_ENABLED=false`. When revenue is sufficient, set it to `true` in the Vercel environment and configure the server-side provider key/model.
- Paid Android products are wired through Google Play Billing 9.1.0.

## Product IDs
Create these exact products in Play Console:
- `starter_10_credits` — one-time consumable — 10 AI credits — Rs. 299
- `creator_50_credits` — one-time consumable — 50 AI credits — Rs. 999
- `pro_100_credits` — one-time consumable — 100 AI credits — Rs. 1,699
- `chaini_pro_monthly` — subscription — Rs. 799/month

The app only starts a real purchase when Google Play returns the configured product. Do not accept real payments until the products are created and tested in Play Console.

## Important production hardening
The current client bridge consumes/acknowledges purchases and updates the UI. For a high-security production release, add server-side purchase-token verification and entitlement storage before relying on paid credits for valuable AI usage. Never put Google service-account credentials in the APK or web frontend.

## AI recovery
Keep `IMAGE_GENERATION_ENABLED=false` during launch testing. Later, after ad/paid revenue is sufficient:
1. Add the provider API key to Vercel server environment variables.
2. Set `IMAGE_GENERATION_ENABLED=true`.
3. Keep the emergency stop available.
4. Test generation on a small daily cap before increasing limits.
