# Domain Value Objects

## Introduction

Value Objects represent controlled business concepts in NexusMarket whose meaning is determined by their values rather than by an independent identity.

They are used to express roles, statuses, classifications, and operation types without relying on arbitrary Strings distributed throughout the application.

This document is based primarily on the official NexusMarket functional specification. Concepts and values not completely defined by the specification remain pending and must not be completed by assumption.

---

# Value Object Principles

- Value Objects do not have an independent business identity.
- Equality is determined by value.
- Allowed values are controlled by the domain.
- Domain classes reference Value Objects instead of arbitrary Strings.
- Value Objects should be immutable when implemented in Java.
- A concept is not considered complete when the specification does not define all of its allowed values.
- The final Java representation as an `enum` or immutable class will be decided before implementation.

---

# Classification Summary

## Complete Value Objects

The following concepts have a sufficiently defined set of allowed values:

- `SystemRole`
- `WarehouseOwnerType`
- `ProductType`
- `ProductStatus`
- `InventoryMovementType`

## Partially Defined Value Objects

The following concepts are required by the domain, but their complete allowed values are not defined:

- `UserStatus`
- `BuyerStatus`
- `InventoryCondition`
- `OrderStatus`

## Unsupported Status Catalogs

The following catalogs must not be created until additional requirements are available:

- `InvoiceStatus`
- `ShipmentStatus`
- `ReturnStatus`
- `RefundStatus`

---

# SystemRole

## Description

Represents the single role assigned to a User within NexusMarket.

The role determines the User's responsibilities and the information they are authorized to manage.

## Used By

- `User.role`

## Allowed Values

| Code | Name | Business Responsibility | Source |
|---|---|---|---|
| `BUYER` | Buyer | Acquires published Products and manages their own purchasing processes. | Explicit |
| `SELLER` | Seller | Registers and manages their own Products and participates in Inventory and Order management. | Explicit |
| `LOGISTICS_OPERATOR` | Logistics Operator | Manages physical Warehouse operations and dispatches. | Explicit |
| `ADMINISTRATOR` | Administrator | Registers Sellers and manages Sellers and Warehouses. | Explicit |
| `SUPERVISOR` | Supervisor | Consults and monitors operational information. | Explicit |

## Business Rules

- Every User must have exactly one SystemRole.
- A User must not manage information outside the responsibilities of their role.
- A SystemRole is not automatically represented as a separate Java class.
- `ADMINISTRATOR`, `LOGISTICS_OPERATOR`, and `SUPERVISOR` remain roles unless independent domain data justifies separate Entities.

---

# UserStatus

## Description

Represents the operational condition of a User in NexusMarket.

The specification requires every User to have a status controlled by a defined catalog and mentions conditions such as Active and Blocked.

## Used By

- `User.status`

## Currently Supported Evidence

| Possible Code | Meaning | Status of Decision |
|---|---|---|
| `ACTIVE` | The User is operational. | Mentioned by the specification as an example. |
| `BLOCKED` | The User is blocked from normal operation. | Mentioned by the specification as an example. |

## Pending Definition

- The specification uses the expression “Active, Blocked, etc.” and therefore does not define the complete catalog.
- Additional values such as `INACTIVE` must not be added solely because they appeared in the banking example.
- Valid status transitions are not defined.
- The final Java implementation must wait until the complete catalog is confirmed.

---

# BuyerStatus

## Description

Represents the commercial condition of a Buyer and their ability to participate in purchasing processes.

The functional specification explicitly requires the Buyer to have a commercial status, but it does not define the allowed values.

## Used By

- `Buyer.commercialStatus`

## Pending Definition

- No allowed BuyerStatus values are defined.
- No valid status transitions are defined.
- `BuyerStatus` remains a required but incomplete candidate Value Object.
- It must not be replaced automatically by `UserStatus`, because operational User status and Buyer commercial status may represent different concepts.

---

# WarehouseOwnerType

## Description

Represents the owner classification of a Warehouse.

The specification distinguishes Warehouses owned by the Marketplace from Warehouses associated with Sellers.

## Used By

- `Warehouse.ownerType`

## Allowed Values

| Code | Name | Description | Source |
|---|---|---|---|
| `MARKETPLACE` | Marketplace Warehouse | Warehouse belonging to the centralized Marketplace operation. | Explicit |
| `SELLER` | Seller Warehouse | Warehouse associated with a Seller. | Explicit |

## Business Rules

- Every Warehouse must have exactly one owner classification.
- A Warehouse classified as `SELLER` must be associated with a Seller.
- The specification does not define a separate Marketplace Entity to reference when the value is `MARKETPLACE`.

---

# ProductType

## Description

Represents the commercial and operational nature of a Product.

Product Type determines whether Inventory and physical dispatch are required.

## Used By

- `Product.productType`

## Allowed Values

| Code | Name | Description | Source |
|---|---|---|---|
| `PHYSICAL` | Physical Product | Product that requires Inventory management and physical dispatch. | Explicit |
| `DIGITAL` | Digital Product | Product that may be delivered immediately after payment confirmation. | Explicit |

## Business Rules

- Every Product must have exactly one ProductType.
- Physical Products require Inventory and dispatch.
- Digital Products may be delivered immediately after payment confirmation.
- The specification does not define the technical mechanism for delivering digital Products.

---

# ProductStatus

## Description

Represents the commercial visibility and availability condition of a Product within the NexusMarket catalog.

## Used By

- `Product.status`

## Allowed Values

| Code | Name | Description | Source |
|---|---|---|---|
| `PUBLISHED` | Published | Product is visible in the public catalog. | Explicit |
| `SUSPENDED` | Suspended | Product is temporarily suspended from normal catalog operation. | Explicit value; detailed behavior not defined |
| `DISCONTINUED` | Discontinued | Product is no longer commercially continued. | Explicit value; detailed behavior not defined |

## Business Rules

- Every Product must have exactly one ProductStatus.
- A Product with `PUBLISHED` status is visible in the public catalog.
- Valid transitions between ProductStatus values are not defined.
- Behavior for suspended or discontinued Products already included in a Cart or Order is not defined.

---

# InventoryMovementType

## Description

Represents the business reason for a change applied to Inventory.

## Used By

- Candidate `InventoryMovement` supporting Entity.

## Allowed Values

| Code | Name | Description | Source |
|---|---|---|---|
| `ENTRY` | Entry | Adds stock to Inventory. | Explicit |
| `RESERVATION` | Reservation | Reserves available stock for a commercial process. | Explicit |
| `SALE_EXIT` | Sale Exit | Removes stock as a consequence of a sale. | Explicit |
| `ADJUSTMENT` | Adjustment | Corrects or adjusts Inventory stock. | Explicit |
| `RETURN` | Return | Adds or processes stock resulting from a Return. | Explicit |

## Business Rules

- Every Inventory change must correspond to an allowed InventoryMovementType.
- Inventory quantities must never become negative.
- A Reservation must not be performed when stock does not exist.
- Inventory marked as damaged must not be reserved.
- The effect of each movement on available, reserved, damaged, or total quantities requires further definition.

---

# InventoryCondition

## Description

Represents the operational condition of Inventory.

This concept is inferred from the critical rule that Inventory marked as damaged must not be reserved.

## Used By

- Candidate `Inventory.condition` attribute.

## Currently Supported Evidence

| Possible Code | Meaning | Status of Decision |
|---|---|---|
| `DAMAGED` | Inventory cannot be reserved because it is damaged. | Explicitly mentioned condition. |

## Pending Definition

- The complete InventoryCondition catalog is not defined.
- Normal, available, reserved, or unavailable conditions must not be added automatically.
- It must be confirmed whether damaged stock is represented through a condition, a separate quantity, or an Inventory Movement.

---

# OrderStatus

## Description

Represents the current stage of the Order lifecycle.

## Used By

- `Order.status`

## Lifecycle Defined by the Specification

```text
CART
  |
  v
PENDING_PAYMENT
  |
  v
PAID
  |
  v
DISPATCHED
  |
  v
DELIVERED / FINALIZED
```

## Allowed or Candidate Values

| Code | Name | Description | Status of Decision |
|---|---|---|---|
| `CART` | Cart | Provisional Product selection. | Explicit stage; representation is pending. |
| `PENDING_PAYMENT` | Pending Payment | Waiting for financial confirmation. | Explicit |
| `PAID` | Paid | Payment is confirmed and preparation begins. | Explicit |
| `DISPATCHED` | Dispatched | Physical Order has left the Warehouse. | Explicit |
| `DELIVERED` | Delivered | Delivery has been successfully completed. | Explicit wording |
| `FINALIZED` | Finalized | Order is closed after confirmed delivery. | Explicit wording; separation from Delivered is unclear. |

## Business Rules

- Order state changes must follow the lifecycle defined by the specification.
- A finalized Order must not be modified under any circumstance.
- Payment confirmation initiates preparation.
- Dispatch applies to physical Products.

## Pending Definition

- It must be confirmed whether `CART` is an OrderStatus or whether Cart is an independent Entity.
- It must be confirmed whether `DELIVERED` and `FINALIZED` are separate states or two names for one final state.
- The lifecycle for digital Products is not completely defined.
- Invalid transition behavior is not defined.

---

# Concepts Not Yet Classified as Value Objects

## ProductVariant

The specification defines variants such as color, size, and model, but does not define their complete structure. `ProductVariant` may later become an Entity or Value Object.

## CartItem

`CartItem` represents a Product and quantity selected in a Cart. Its final classification as Entity or Value Object remains pending.

## OrderItem

`OrderItem` represents a Product and quantity included in an Order. Its final classification as Entity or Value Object remains pending.

## Address

The specification defines primary and additional Buyer addresses as data but does not require an `Address` Value Object. They remain Strings until additional structure is required.

---

# Status Catalogs Not Supported by the Specification

The following catalogs appeared in earlier proposals or may seem normal in a Marketplace, but their values are not defined by the official specification:

## InvoiceStatus

- No Invoice lifecycle or status values are defined.
- Values such as `PENDING`, `PAID`, or `VOIDED` must not be adopted without confirmation.

## ShipmentStatus

- No Shipment lifecycle or status values are defined.
- Preparation, dispatch, transport, and delivery are business activities, but the specification does not formally define them as ShipmentStatus values.

## ReturnStatus

- No Return lifecycle or status values are defined.
- Approval, rejection, and completion values must not be invented.

## RefundStatus

- No Refund lifecycle or status values are defined.
- Financial processing states must not be invented.

---

# Entity-to-Value-Object Mapping

| Domain Entity | Value Object Attribute | Status |
|---|---|---|
| `User` | `SystemRole role` | Complete |
| `User` | `UserStatus status` | Incomplete catalog |
| `Buyer` | `BuyerStatus commercialStatus` | Incomplete catalog |
| `Warehouse` | `WarehouseOwnerType ownerType` | Complete |
| `Product` | `ProductType productType` | Complete |
| `Product` | `ProductStatus status` | Complete |
| `Inventory` | `InventoryCondition condition` | Inferred and incomplete |
| `InventoryMovement` | `InventoryMovementType movementType` | Complete values; supporting Entity pending |
| `Order` | `OrderStatus status` | Values partially ambiguous |

---

# Implementation Decision Pending

The banking repository implements business catalogs as immutable classes derived from a `DomainCatalog` base class. Earlier classroom examples also used Java `enum` types for some statuses.

For NexusMarket, the following decision remains pending:

```text
Option A: Java enum
Option B: Immutable Value Object class
Option C: DomainCatalog hierarchy
```

The final representation must be consistent across the NexusMarket domain and must follow the professor's delivery requirements.

Regardless of the Java representation, the business meaning and allowed values documented here remain part of the Domain Model.

---

# Pending Confirmations

1. Complete allowed values of `UserStatus`.
2. Complete allowed values of `BuyerStatus`.
3. Complete representation and values of `InventoryCondition`.
4. Whether `CART` belongs to `OrderStatus` or to an independent Cart Entity.
5. Whether `DELIVERED` and `FINALIZED` are separate Order statuses.
6. Whether `ProductVariant`, `CartItem`, and `OrderItem` are Entities or Value Objects.
7. Whether Invoice, Shipment, Return, and Refund require status catalogs.
8. Whether Value Objects will be implemented as enums, immutable classes, or a DomainCatalog hierarchy.

---

# Domain Value Object Design Rules

- Do not represent controlled business concepts with arbitrary Strings.
- Do not create allowed values that are not supported by the functional specification or professor clarification.
- Keep complete catalogs separate from incomplete catalogs.
- Value Objects must not depend on Spring, JPA, MongoDB, HTTP, or other infrastructure technologies.
- Value Objects should be immutable.
- Domain Entities must reference the appropriate Value Object type.
- Status transitions must follow explicit business rules and must not be invented.
- Technical enumerations must remain separate from business Value Objects when that distinction becomes necessary.
