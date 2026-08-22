# Domain Value Objects

## Introduction

Value Objects represent controlled business concepts in NexusMarket whose meaning is determined by their values rather than by an independent identity.

This document is based on the official functional specification. Values explicitly named by the specification are marked as **Explicit**. Values required to complete a coherent implementation are marked as **Inferred**.

The banking repository is used only as a documentation and structure guide.

---

## Design Principles

- Value Objects do not have an independent business identity.
- Equality is determined by value.
- Controlled catalogs are implemented as Java `enum` types.
- Structured Value Objects are implemented as immutable Java classes.
- Value Objects do not depend on Spring, JPA, MongoDB, HTTP, or JSON.
- Inferred values must be traceable to a requirement or critical validation.
- Catalogs unsupported by the specification must not be created.

---

## Classification Summary

### Implemented Enums

- `SystemRole`
- `UserStatus`
- `BuyerStatus`
- `WarehouseOwnerType`
- `ProductType`
- `ProductStatus`
- `InventoryMovementType`
- `InventoryCondition`
- `OrderStatus`

### Implemented Immutable Value Objects

- `ProductVariant`

### Unsupported Status Catalogs

The following catalogs are intentionally excluded because the specification does not define their values:

- `InvoiceStatus`
- `ShipmentStatus`
- `ReturnStatus`
- `RefundStatus`

---

# SystemRole

## Description

Represents the single role assigned to a User. Each concrete User specialization returns exactly one role.

## Allowed Values

| Code | Responsibility | Source |
| --- | --- | --- |
| `BUYER` | Acquires published Products and manages their own purchasing process. | Explicit |
| `SELLER` | Registers and manages Products and participates in Inventory and Order management. | Explicit |
| `LOGISTICS_OPERATOR` | Manages physical Warehouse operations and dispatches. | Explicit |
| `ADMINISTRATOR` | Registers Sellers and manages Sellers, Warehouses, and Refunds. | Explicit |
| `SUPERVISOR` | Consults and monitors operational information. | Explicit |

## Business Rules

- Every User has exactly one role.
- A participant must not administer information outside that role.
- `Buyer`, `Seller`, `LogisticsOperator`, `Administrator`, and `Supervisor` specialize `User` and return a constant role.

---

# UserStatus

## Description

Represents the operational condition of a User.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `ACTIVE` | User is operational and may act according to their role. | Explicit wording |
| `INACTIVE` | User is operationally disabled without being blocked. | Inferred |
| `BLOCKED` | User is blocked from normal operation. | Explicit wording |

## Business Rules

- Every User must have one non-null status.
- Only active participants may execute protected operational actions in the implemented domain.
- `UserStatus` is different from `BuyerStatus`.

---

# BuyerStatus

## Description

Represents the Buyer's commercial ability to participate in purchases.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `ACTIVE` | Buyer is commercially enabled to purchase. | Inferred from required commercial status |
| `SUSPENDED` | Buyer is temporarily prevented from purchasing. | Inferred from the commercial participation rule |

## Business Rules

- A Buyer can confirm a purchase only when both `UserStatus` and `BuyerStatus` are `ACTIVE`.
- A suspended Buyer may not create a formal Order from a Cart.

---

# WarehouseOwnerType

## Description

Represents the owner classification of a Warehouse.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `MARKETPLACE` | Warehouse belonging to the centralized Marketplace operation. | Explicit |
| `SELLER` | Warehouse associated with a Seller. | Explicit |

## Business Rules

- Every Warehouse has one owner classification.
- A Seller may only be associated with `SELLER` Warehouses.
- A Seller is incorporated with their first Seller Warehouse.

---

# ProductType

## Description

Determines the commercial and operational nature of a Product.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `PHYSICAL` | Requires Inventory and physical dispatch. | Explicit |
| `DIGITAL` | May be delivered after payment without physical dispatch. | Explicit |

---

# ProductStatus

## Description

Represents the commercial visibility and availability of a Product in the catalog.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `PUBLISHED` | Visible and selectable in the public catalog. | Explicit |
| `SUSPENDED` | Temporarily unavailable in the catalog. | Explicit |
| `DISCONTINUED` | Removed from continued commercialization. | Explicit |

## Business Rules

- Only published Products may be added to a Cart.
- Every Product must have one ProductType and one ProductStatus.

---

# ProductVariant

## Description

Represents a Product variation such as color, size, model, or a combination of characteristics.

The specification provides examples but does not define a mandatory variant schema. Therefore, the implementation uses a required immutable description.

## Java Representation

```text
ProductVariant
  - description: String
```

## Business Rules

- Description must not be null, empty, or blank.
- Equality is based on the normalized description.
- A Product must not contain duplicate variants.
- ProductVariant is an immutable Value Object.

## Source

- Variant concept and examples: Explicit.
- Description-based immutable representation: Inferred technical design.

---

# InventoryMovementType

## Description

Represents the reason or operation that produces an Inventory change.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `ENTRY` | Entry of stock into Inventory. | Explicit |
| `RESERVATION` | Reservation of available stock. | Explicit |
| `SALE_EXIT` | Stock exit caused by a sale. | Explicit |
| `ADJUSTMENT` | Operational correction of Inventory. | Explicit |
| `RETURN` | Stock entry associated with a Return. | Explicit |

## Business Rules

- Every InventoryMovement must use one of these values.
- A movement references Inventory, a positive quantity, and the User who performed it.
- Only an active Seller or Logistics Operator may register an Inventory movement.

---

# InventoryCondition

## Description

Represents whether Inventory may participate in a reservation.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `AVAILABLE` | Inventory may be reserved when sufficient quantity exists. | Inferred normal condition |
| `DAMAGED` | Inventory cannot be reserved. | Explicit critical validation |

## Business Rules

- Inventory must always have one condition.
- Damaged Inventory must not be reserved.
- Inventory quantity must never become negative.
- Nonexistent stock must not be reserved.

---

# OrderStatus

## Description

Represents the current stage of the formal Order lifecycle.

## Allowed Values

| Code | Meaning | Source |
| --- | --- | --- |
| `PENDING_PAYMENT` | Formal Order is waiting for payment confirmation. | Explicit |
| `PAID` | Payment is confirmed and preparation may begin. | Explicit |
| `DISPATCHED` | Physical Order has left the Warehouse. | Explicit |
| `DELIVERED` | Delivery has been confirmed. | Explicit wording |
| `FINALIZED` | Order is closed after delivery and becomes immutable. | Explicit wording and critical rule |

## Resolved Lifecycle

```text
Cart --confirmation--> PENDING_PAYMENT
                           |
                           v
                          PAID
                           |
             +-------------+-------------+
             |                           |
      Physical Order                Digital Order
             |                           |
             v                           |
        DISPATCHED                       |
             |                           |
             +-------------> DELIVERED <-+
                                  |
                                  v
                              FINALIZED
```

## Resolved Decisions

- `Cart` is an independent Entity and is not an OrderStatus.
- `DELIVERED` and `FINALIZED` are separate states.
- Digital-only Orders do not use `DISPATCHED`.
- A finalized Order cannot change under any circumstance.
- State transitions are controlled by Order behavior; no generic `setStatus` exists.

---

# Supporting Concepts Classified

| Concept | Classification | Rationale |
| --- | --- | --- |
| `ProductVariant` | Immutable Value Object | Defined by its descriptive value and has no independent lifecycle. |
| `CartItem` | Mutable internal model of Cart | Quantity may change while the selection remains provisional. |
| `OrderItem` | Immutable internal model of Order | Confirmed Product and quantity must not change with later Cart modifications. |
| `Address` | String at this stage | The specification does not define a structured Address schema. |

---

# Entity-to-Value-Object Mapping

| Domain Model | Value Object | Implementation Status |
| --- | --- | --- |
| `User` | `SystemRole` | Implemented |
| `User` | `UserStatus` | Implemented |
| `Buyer` | `BuyerStatus` | Implemented |
| `Warehouse` | `WarehouseOwnerType` | Implemented |
| `Product` | `ProductType` | Implemented |
| `Product` | `ProductStatus` | Implemented |
| `Product` | `ProductVariant` | Implemented |
| `Inventory` | `InventoryCondition` | Implemented |
| `InventoryMovement` | `InventoryMovementType` | Implemented |
| `Order` | `OrderStatus` | Implemented |

---

# Deliberately Unsupported Catalogs

The following catalogs are not implemented because the functional specification includes their business processes but does not define lifecycle values:

## InvoiceStatus

Invoice exists as a Domain model associated with a paid Order, but no Invoice status catalog is defined.

## ShipmentStatus

Shipment behavior derives dispatch and delivery information from OrderStatus. No independent Shipment status catalog is defined.

## ReturnStatus

Return exists as a Domain model for a delivered or finalized Order, but approval or rejection values are not defined.

## RefundStatus

Refund exists as a Domain model processed by an active Administrator, but financial processing states are not defined.

---

# Java Representation Decision

The implementation decision is resolved as follows:

- Finite controlled catalogs use Java `enum`.
- Structured Value Objects use immutable final classes.
- `ProductVariant` implements value equality through `equals` and `hashCode`.
- No Value Object contains Spring, persistence, or transport annotations.

---

# Remaining Open Questions

The following items cannot be resolved from the current specification and remain outside the implemented Value Object scope:

1. Whether Inventory will eventually be managed per Product or per ProductVariant.
2. Whether Invoice, Shipment, Return, or Refund receive status catalogs in a future requirement.
3. Whether Address receives a structured schema.
4. Whether monetary concepts such as price, amount, tax, or refund amount will be formally specified.

---

# Domain Value Object Design Rules

- Controlled concepts must not use arbitrary Strings when a catalog is defined.
- Explicit and inferred values must remain distinguishable in documentation.
- Value Objects must be immutable.
- Entity state transitions must use domain behavior rather than unrestricted setters.
- Unsupported catalogs must not be invented.
- New requirements must update this document before implementation.
