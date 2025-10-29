# Splitwise in telegram

## Technologies:
- JAVA, Spring Boot,
- PostgreSQL (to save appUser's info, spendings)
- Telegram Bot API

##  
1) User input their name, save it in DB
2) User input spendings -> save it in DB 
3) Connect to Telegram Bot API
4) appUser can get their own spendings, but must not see spendings of other users.

## TODO

- [ ] Chat ID vs User ID

Currently, users are registered by userId but retrieved by chatId.
Fix: consistently distinguish between chatId (conversation context) and userId (actual user).
Store conversation state by chatId, but link expenses to userId.

- [ ] Group Chat Behavior

In groups, chatId ≠ userId, which breaks logic.
Fix: store conversation states as (chatId, userId) pairs or disable multi-user flows in groups.

- [ ] Currency Handling / add exchanging feature

showUserExpenses sums all expenses regardless of currency.
Fix: group totals by currency or convert to a base currency.

- [ ] Commands like /help or /cancel don’t interrupt ongoing flows.

Fix: implement global command handling (high-priority commands that work in any state).

- [ ] “Skip” Currency Handling

When user skips currency, the field can be null.
Fix: assign a default currency (e.g., user or chat default) or make the DB column nullable.

- [ ] Locale and Number Parsing

Input like 12,50 causes parsing errors.
Fix: support locale-aware parsing and trim spaces.

- [ ] Transaction Safety?

Expense saving and conversation deletion happen separately.
Fix: wrap processCurrency() in a @Transactional block to ensure atomicity.

- [ ] Error & Fallback Handling

- [ ] Add /cancel command to abort any flow.

- [ ] The main switch in SplitMoneyBot is becoming large.

Fix: refactor into a registry of StateHandler or CommandHandler classes.