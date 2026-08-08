(function() {
    // GUARD 1: Check Publix Data Layer for authentication state
    const isLoggedOut = window.pblxDataLayer &&
                        window.pblxDataLayer.user &&
                        window.pblxDataLayer.user.user_state !== "Logged-In";

    // GUARD 2: Fallback check for visible Log In button
    const hasLoginButton = Array.from(document.querySelectorAll('a, button')).some(el =>
        (el.textContent.trim().toLowerCase() === 'log in' || el.textContent.trim().toLowerCase() === 'sign in') &&
        el.offsetParent !== null
    );

    if (isLoggedOut || hasLoginButton) {
        console.log("User is not logged in. Aborting clip and requesting login flow.");
        AndroidBridge.onSessionInvalid("https://www.publix.com/login");
        return;
    }

    // If guards pass, proceed to clipping
    const INITIAL_WAIT_TIME = 5000;
    setTimeout(() => { startClipping(); }, INITIAL_WAIT_TIME);

    function simulateHumanClick(element) {
        ['mouseenter', 'mousedown', 'mouseup', 'click'].forEach(eventType => {
            const event = new MouseEvent(eventType, { bubbles: true, cancelable: true, view: window, buttons: 1 });
            element.dispatchEvent(event);
        });
    }

    async function startClipping() {
        const delay = (ms) => new Promise(res => setTimeout(res, ms));
        let keepGoing = true;

        while (keepGoing) {
            const allClipButtons = Array.from(document.querySelectorAll('button[data-qa-automation="button-Clip coupon"]'));
            const couponsToClip = allClipButtons.filter(btn => btn.textContent.trim().toLowerCase().includes("clip coupon"));

            if (couponsToClip.length > 0) {
                for (let i = 0; i < couponsToClip.length; i++) {
                    couponsToClip[i].scrollIntoView({ behavior: "smooth", block: "center" });
                    await delay(150);
                    simulateHumanClick(couponsToClip[i]);
                    await delay(350);
                }
                await delay(1000);
            }

            const allLoadMoreBtns = Array.from(document.querySelectorAll('button[data-qa-automation="button-Load more"]'));
            const visibleLoadMoreBtn = allLoadMoreBtns.find(btn => btn.offsetParent !== null && !btn.disabled);

            if (visibleLoadMoreBtn) {
                visibleLoadMoreBtn.scrollIntoView({ behavior: "smooth", block: "center" });
                await delay(800);
                simulateHumanClick(visibleLoadMoreBtn);

                let foundNew = false;
                for (let i = 0; i < 20; i++) {
                    await delay(500);
                    const newButtonsCheck = Array.from(document.querySelectorAll('button[data-qa-automation="button-Clip coupon"]'));
                    const newCoupons = newButtonsCheck.filter(btn => btn.textContent.trim().toLowerCase().includes("clip coupon"));

                    if (newCoupons.length > 0) {
                        foundNew = true;
                        break;
                    }
                }
                if (!foundNew) keepGoing = false;
            } else {
                keepGoing = false;
            }
        }
        AndroidBridge.onClippingComplete("Publix");
    }
})();