# 💰 PlaceholderTebex

> Real-time Tebex store data exposed as PlaceholderAPI variables — top donors, last donations, goals, stats and more. Fully configurable via `config.yml`.

---

## 📋 Requirements

| Dependency | Version |
|---|---|
| Paper / Spigot | 1.20.x — 1.21.x |
| PlaceholderAPI | 2.11.x+ |
| Java | 17+ |

---

## ⚙️ Installation

1. Drop `PlaceholderTebex.jar` into your `/plugins` folder.
2. Start the server once to generate `config.yml`.
3. Open `plugins/PlaceholderTebex/config.yml` and set your API key.
4. Run `/ptebex reload`.

### Where to get your Tebex API Key

1. Go to [creator.tebex.io](https://creator.tebex.io)
2. Navigate to **Integrations** → **API Keys**
3. Copy your **Private Key** (click Generate if empty)
4. Paste it into `config.yml` under `tebex.secret-key`

> ⚠️ Never share your Private Key publicly. It gives full access to your store.

---

## 🔧 config.yml

```yaml
tebex:
  secret-key: "YOUR_PRIVATE_KEY_HERE"
  base-url: "https://plugin.tebex.io"

cache:
  refresh-interval: 10        # Minutes between Tebex API requests
  payment-history-limit: 100  # How many past payments to load

goal:
  enabled: true
  target-amount: 500.0        # Monthly donation goal

placeholders:
  no-data: "N/A"              # Shown when data isn't available
  loading: "Loading..."       # Shown while cache is warming up
  currency-format: "%.2f"     # Java format string for amounts
  date-format: "dd/MM/yyyy"
  datetime-format: "dd/MM/yyyy HH:mm"
```

---

## 📌 Placeholders

### 🕐 Last Donor

| Placeholder | Description |
|---|---|
| `%tebex_last_donor_name%` | Username of the last donor |
| `%tebex_last_donor_amount%` | Amount they paid |
| `%tebex_last_donor_currency%` | Currency used |
| `%tebex_last_donor_date%` | Date of the donation |
| `%tebex_last_donor_datetime%` | Date + time of the donation |
| `%tebex_last_donor_package%` | Package they purchased |
| `%tebex_last_donor_transaction%` | Transaction ID |

---

### 🏆 All-Time Top Donors (1–10)

| Placeholder | Description |
|---|---|
| `%tebex_top_name_1%` → `_10%` | Username of top donor #N |
| `%tebex_top_amount_1%` → `_10%` | Total amount donated by top #N |
| `%tebex_top_currency_1%` → `_10%` | Currency of top donor #N |
| `%tebex_top_date_1%` → `_10%` | Date of top donor #N's last payment |
| `%tebex_top_package_1%` → `_10%` | Package purchased by top donor #N |

---

### 📅 Monthly Top Donors (1–10)

| Placeholder | Description |
|---|---|
| `%tebex_monthly_top_name_1%` → `_10%` | Username of monthly top #N |
| `%tebex_monthly_top_amount_1%` → `_10%` | Amount donated this month by #N |
| `%tebex_monthly_top_currency_1%` → `_10%` | Currency |
| `%tebex_monthly_top_date_1%` → `_10%` | Date of donation |
| `%tebex_monthly_top_package_1%` → `_10%` | Package purchased |
| `%tebex_monthly_total%` | Total donated this month |
| `%tebex_monthly_transactions%` | Number of transactions this month |

---

### 📊 General Stats

| Placeholder | Description |
|---|---|
| `%tebex_total_donated%` | All-time total donated |
| `%tebex_total_donated_month%` | Total donated this month |
| `%tebex_total_donated_today%` | Total donated today |
| `%tebex_total_transactions%` | All-time transaction count |
| `%tebex_total_transactions_month%` | Transactions this month |
| `%tebex_total_transactions_today%` | Transactions today |
| `%tebex_unique_donors%` | Number of unique donors |

---

### 🎯 Donation Goal

| Placeholder | Description |
|---|---|
| `%tebex_goal_target%` | Target amount set in config |
| `%tebex_goal_current%` | Amount raised this month |
| `%tebex_goal_remaining%` | How much is left to reach the goal |
| `%tebex_goal_progress%` | Percentage with `%` symbol (e.g. `73.50%`) |
| `%tebex_goal_progress_raw%` | Raw percentage number (e.g. `73.50`) |
| `%tebex_goal_bar%` | Visual bar 20 chars (e.g. `▓▓▓▓▓▓▓▓░░░░░░░░░░░░`) |
| `%tebex_goal_bar_10%` | Visual bar 10 chars |
| `%tebex_goal_reached%` | `true` or `false` |

---

### 🏪 Store Info

| Placeholder | Description |
|---|---|
| `%tebex_store_name%` | Your store name |
| `%tebex_store_currency%` | Store default currency |
| `%tebex_store_domain%` | Store domain |

---

## 💬 Commands

| Command | Description |
|---|---|
| `/ptebex reload` | Reload config and restart cache task |
| `/ptebex refresh` | Force fetch fresh data from Tebex now |
| `/ptebex clearcache` | Wipe all cached data |
| `/ptebex info` | Show plugin status, donor count, goal info |

**Permission:** `placeholdertebex.admin` (default: op)

---

## 💡 Usage Examples

**Hologram with top donors:**
```
&6&l TOP DONORS
&e#1 &f%tebex_top_name_1% &7- &a$%tebex_top_amount_1%
&e#2 &f%tebex_top_name_2% &7- &a$%tebex_top_amount_2%
&e#3 &f%tebex_top_name_3% &7- &a$%tebex_top_amount_3%
```

**Monthly goal bar:**
```
&eDonation Goal: &f%tebex_goal_bar% &7%tebex_goal_progress%
```

**Last donor board:**
```
&eLast donation: &f%tebex_last_donor_name% &7» &a$%tebex_last_donor_amount%
&7%tebex_last_donor_datetime%
```

---

## 🔄 How Cache Works

PlaceholderTebex fetches data from Tebex on startup and then every N minutes (configurable). All placeholders are served from memory — no API call happens per placeholder request. This keeps your server fast and stays within Tebex API rate limits.

Use `/ptebex refresh` to force an instant update at any time.

---

## 📦 Source Code

GitHub: [github.com/YesithTK](https://github.com/YesithTK)

---

## 💬 Support

Discord: **_onlynea**
