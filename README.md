# CS2114-project2-group86
Project 2 repo
# ShelfCheck – Grocery Stock Tracker (Prototype)

CS 2114 · Project 2 · Deliverable 3 · Group 86
Raul Garuz, Thomas Cherian, Karthik Mittadhoddi

ShelfCheck tracks a small grocery store's stock and sales. The manager adds
shipments, rings up a customer's items as one sale, looks up how much of each
product is on the shelves, and sees total units and revenue. All data is stored
in the course's generic `LinkedChain<T>`, reused for `Item`, `Sale` and `String`.

## Project layout

```
src/    LinkedChain.java (provided, unchanged), Item.java, Sale.java,
        Inventory.java, InputReader.java, ShelfCheckApp.java
test/   ItemTest.java, SaleTest.java, LinkedChainTest.java,
        InventoryTest.java, InputReaderTest.java, ShelfCheckAppTest.java
lib/    junit-platform-console-standalone-<version>.jar
```

## How to compile and run

**Program** (from the repository folder):

```
javac -d bin src/*.java
java -cp bin ShelfCheckApp
```

**Tests** (JUnit 5). Download `junit-platform-console-standalone` from Maven
Central into `lib/`, then:

```
javac -d bin -cp lib/junit-platform-console-standalone-1.10.2.jar src/*.java test/*.java
java -jar lib/junit-platform-console-standalone-1.10.2.jar -cp bin --scan-class-path
```

**In VS Code:** with the Extension Pack for Java installed, open the Testing
panel (flask icon), choose **JUnit Jupiter** if asked, and click **Run All Tests**.
Run the program with the **Run** button above `main` in `ShelfCheckApp.java`.

**Menu:** 1 Add stock · 2 New sale · 3 Look up item · 4 Inventory ·
5 Total units · 6 Sales summary · 7 Best sellers · 0 Quit

## Reflection

### What changed from our specification?

| Area | Spec said | We built | Why |
| --- | --- | --- | --- |
| ShelfCheckApp | Only `main` | Constructor `ShelfCheckApp(Inventory, Scanner)` and `run()`; `main` just creates the app and calls `run()` | Lets JUnit drive the whole menu with input from a String instead of a person typing |
| Sale | Copies the cart | Copies it by walking the cart's array backwards with `add()` | `addToEnd()` would make the copy O(k²); this keeps it O(k), as the spec promised |
| Sale | No way to read its lines | Added `getItems()` (returns a copy) | Needed for the best-sellers report |
| Inventory | MVP methods only | Added `getUnitsSold(name)` and `getBestSellerReport()`, plus menu option 7 | Popularity stretch goal, which is easy once sales store their items |
| Validation | Menu choice 9 → "Invalid option." | Re-prompts with "Enter a number from 0 to 7." | Same response (re-prompt); one shared message from `InputReader.readInt` |
| New sale | Quote on an empty inventory throws | The app checks for an empty inventory before asking for items | Friendlier; the exception is still in `Inventory.quote` and still tested |
| Tests | Fixed time `t` in the plan | Added a package-private `checkout(cart, time)` | So sale times in tests are repeatable |
| Early prototype | — | An early category prototype (`GroceryInventory`) used its own node classes; we removed it | The rubric requires all storage to go through `LinkedChain`. Categories will be rebuilt on `LinkedChain<Category>` (see Next Steps) |

[TODO: add anything else that changed while Thomas and Karthik built their classes]

### What changed in the LinkedChain?

Nothing. We used the provided `LinkedChain.java` unchanged. Our classes only call
`add`, `addToEnd`, `remove(T)`, `count`, `contains`, `size`, `isEmpty` and
`toArray(T[])`. We loop over a chain with `toArray(new Item[chain.size()])` (O(n))
instead of calling `get(i)` in a loop (O(n²)). `add` puts new entries at the front,
so cart lines use `addToEnd` to keep receipts in scan order.

### What happened to our unknowns?

| Unknown from our scope | Status |
| --- | --- |
| How bags and linked lists work together | **Resolved.** `LinkedChain` *is* the bag. Our classes hold `LinkedChain` fields and call its methods. |
| Overriding `equals()` so `contains`/`count`/`remove` work on our objects | **Resolved.** `Item` and `Sale` override `equals` and `hashCode`; `LinkedChainTest.testCountUsesEquals` and `SaleTest.testEqualsAnyOrderSameMinute` prove it. |
| Reading input with `Scanner` without crashing | **Resolved.** `InputReader` reads whole lines with `nextLine()` and parses them, catching `NumberFormatException` and re-prompting. |
| Selling by weight (`double weight`) | **Still open.** Not designed yet (stretch goal). |

### What doesn't work yet?

- Data isn't saved between runs; quitting loses everything.
- A product's price can't change while it's in stock (a second price is refused).
- Stretch goals not built yet: categories, low-stock alerts, selling by weight, delivery planner.
- [TODO: list any MVP feature, bad-input case or test that is missing or failing when you submit. Be honest; the rubric asks for this.]

## Next Steps

### What is left to finish the MVP? (in order)

1. [TODO: anything still missing from the MVP when you submit]
2. Run the whole Test Plan from our specification by hand through the menu.
3. Each owner reviews their class's Javadoc and error messages.

### Which stretch goals will we attempt?

| Stretch goal | Decision | Why |
| --- | --- | --- |
| Popularity (best sellers) | **Done** in this prototype | Built on `Sale.getUnitsSold` |
| Categories (Fruit → Apples → …) | **Committing** | Rebuild Thomas's category idea as `LinkedChain<Category>`, each holding a `LinkedChain<Item>`, so a fourth element type reuses the same class |
| Low-stock alerts | **Committing** | Small addition to `Inventory`: a minimum per product and a warning after checkout |
| Sell by weight | **Dropping** | Changes every quantity check from `int` to `double`; too large for the time left |
| Delivery planner | **Dropping** | Needs schedules and dates; not related to the MVP |

[TODO: confirm these decisions with the team]

### What new tests will we need?

- **Category:** creating one (normal; blank name → exception), adding an item, finding a category that doesn't exist (returns null or throws, per what we decide).
- **Inventory with categories:** stock reported per category; looking up an item in a missing category.
- **Low-stock alerts:** setting a minimum (normal; negative minimum and unknown product → exception); an alert appears after a checkout drops stock below the minimum; no alert when it stays above.
- **ShelfCheckApp:** new menu options, including bad choices.

### Who does what?

| Member | Remaining work |
| --- | --- |
| Raul Garuz | `Category` class and its tests |
| Thomas Cherian | Categories and low-stock alerts in `Inventory`, plus their tests |
| Karthik Mittadhoddi | New menu options in `ShelfCheckApp` and their tests; final integration |

[TODO: confirm with the team]

## Attribution

| File | Who | How |
| --- | --- | --- |
| `LinkedChain.java` | CS 2114 staff | Provided; used unchanged |
| `Item.java` | Raul Garuz | Drafted with GenAI (Claude) from our spec; [TODO: e.g. reviewed, run and tested by Raul] |
| `Sale.java` | Raul Garuz | Drafted with GenAI (Claude) from our spec; [TODO] |
| `Inventory.java` | Thomas Cherian | [TODO: e.g. wrote / revised a GenAI draft / reviewed / tested] |
| `InputReader.java` | Karthik Mittadhoddi | [TODO] |
| `ShelfCheckApp.java` | Karthik Mittadhoddi | [TODO] |
| `ItemTest.java` | Raul Garuz | Drafted with GenAI (Claude) from our test plan; [TODO: run and confirmed passing by Raul] |
| `SaleTest.java` | Raul Garuz | Drafted with GenAI (Claude) from our test plan; [TODO] |
| `LinkedChainTest.java` | Raul Garuz | Drafted with GenAI (Claude) from our test plan; [TODO] |
| `InventoryTest.java` | Thomas Cherian | [TODO] |
| `InputReaderTest.java` | Karthik Mittadhoddi | [TODO] |
| `ShelfCheckAppTest.java` | Karthik Mittadhoddi | [TODO] |
| `README.md` | [TODO] | Drafted with GenAI (Claude); [TODO: edited by …] |

### How we used GenAI

We used Claude (Anthropic) during this deliverable. We gave it our submitted
specification and the provided `LinkedChain.java`, and it drafted classes, JUnit
tests and this README following the spec's signatures and test plan. It also
reviewed an early category prototype and pointed out that it bypassed
`LinkedChain`, which the rubric doesn't allow. [TODO: say what you checked or
changed yourselves, e.g. "we ran every test", "we traced checkout by hand",
"we rewrote X because …".]
