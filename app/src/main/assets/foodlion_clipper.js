(function() {
    // GUARD: Abort immediately if we are on a login page or form
    if (window.location.href.includes('login') || document.querySelector('input[type="password"]')) {
        console.log("Login page detected, aborting script.");
        return;
    }

    const INITIAL_WAIT_TIME = 5000;
    setTimeout(() => { startClipping(); }, INITIAL_WAIT_TIME);

    const randomDelay = (min, max) => new Promise(res => setTimeout(res, Math.floor(Math.random() * (max - min + 1)) + min));

    function simulateHumanClick(element) {
        ['mouseenter', 'mousedown', 'mouseup', 'click'].forEach(eventType => {
            const event = new MouseEvent(eventType, { bubbles: true, cancelable: true, view: window, buttons: 1 });
            element.dispatchEvent(event);
        });
    }

    async function startClipping() {
        let keepGoing = true;

        while (keepGoing) {
            const allButtons = Array.from(document.querySelectorAll('button'));
            const couponsToClip = allButtons.filter(btn => btn.textContent.trim().toLowerCase().includes("clip coupon"));

            if (couponsToClip.length > 0) {
                for (let i = 0; i < couponsToClip.length; i++) {
                    couponsToClip[i].scrollIntoView({ behavior: "smooth", block: "center" });
                    await randomDelay(400, 900);
                    simulateHumanClick(couponsToClip[i]);
                    await randomDelay(500, 1200);
                }
                await randomDelay(1500, 3000);
            }

            const showMoreBtn = document.getElementById('show-more');

            if (showMoreBtn && showMoreBtn.offsetParent !== null && !showMoreBtn.disabled) {
                showMoreBtn.scrollIntoView({ behavior: "smooth", block: "center" });
                await randomDelay(600, 1300);
                simulateHumanClick(showMoreBtn);

                let foundNew = false;
                for (let i = 0; i < 15; i++) {
                    await randomDelay(500, 900);
                    const newButtonsCheck = Array.from(document.querySelectorAll('button'));
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
        AndroidBridge.onClippingComplete("Food Lion");
    }
})();