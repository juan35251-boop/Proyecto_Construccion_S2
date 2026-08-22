# Domain Model

## Introduction

The Domain Model represents the main business concepts, relationships, responsibilities, and rules of NexusMarket.

NexusMarket is a centralized Marketplace that acts as a commercial intermediary between buyers and sellers. The system manages users, buyers, sellers, warehouses, products, distributed inventory, shopping carts, orders, billing, shipments, returns, refunds, and administrative queries.

This model is based primarily on the official functional specification of NexusMarket. The banking repository is used only as a structural and documentation guide.

The model follows Object-Oriented Programming and Domain-Driven Design principles. Business concepts explicitly defined in the functional specification are distinguished from design decisions and technical inferences.

---

# Main Business Concepts

The functional specification identifies the following principal business concepts:

- User
- Buyer
- Seller
- Warehouse
- Product
- Inventory
- Cart
- Order
- Invoice
- Shipment
- Return
- Refund

The specification also identifies supporting concepts that require further analysis:

- Product Variant
- Inventory Movement
- Cart Item
- Order Item
- Administrative Report

---

# Business Participants

NexusMarket defines the following business participants:

| Participant | Responsibility |
|---|---|
| Buyer | Acquires published products and manages their own purchasing processes. |
| Seller | Registers and manages their products. |
| Logistics Operator | Manages physical warehouse operations and order dispatches. |
| Administrator | Registers sellers and manages sellers and warehouses. |
| Supervisor | Consults and monitors operational information. |

Every participant performs a single role within the system and may only interact with information related to that role.

---

# Initial Design Considerations

- The functional specification is the primary source of truth.
- A business participant is not automatically considered a Java class.
- Roles, statuses, and controlled types will be analyzed as Value Objects.
- Inheritance will only be used when a genuine specialization relationship exists.
- Relationships and cardinalities will be defined before implementing the Java classes.
- Concepts not defined by the specification will be marked as inferred or pending confirmation.

---

# Entities

# User

## Description

Represents a person authorized to interact with NexusMarket.

A User has a unique identity, a single role, and an operational status. The role determines the responsibilities and information that the User may manage within the Marketplace.

The technical authentication mechanism is outside the functional specification. Therefore, authentication credentials such as username and password are not defined at this stage.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| identification | `String` | Unique identification of the User within NexusMarket. | Explicit |
| fullName | `String` | Official full name of the User. | Explicit |
| email | `String` | Primary access and communication email. | Explicit |
| role | `SystemRole` | Single role assigned to the User. | Explicit |
| status | `UserStatus` | Current operational status of the User. | Explicit |

## Relationships

- A User has exactly one `SystemRole`.
- A User has exactly one `UserStatus`.
- A User may participate in business operations according to their assigned role.
- `User` is an abstract Entity specialized by `Buyer`, `Seller`, `LogisticsOperator`, `Administrator`, and `Supervisor`.
- Each concrete specialization returns exactly one `SystemRole`.

## Business Rules

- The identification must be unique within NexusMarket.
- The email must be unique within NexusMarket.
- The full name must not be empty.
- Every User must have exactly one role.
- Every User must have an operational status.
- Every operation must be executed by an authenticated User.
- A User must not manage information outside the responsibilities of their role.

## Design Notes

- `User` is considered an Entity because it has a unique identity and maintains an operational state.
- The specification does not define username, password, phone number, or address as general User attributes.
- `User` is implemented as an abstract class. Identification is immutable, while full name, email, and operational status change through validated domain methods.

---

# Buyer

## Description

Represents a person who acquires products published in NexusMarket.

The Buyer participates in commercial processes by selecting products through a shopping cart, confirming orders, receiving deliveries, and participating in refund processes when applicable.

The functional specification describes Buyers as Users who make purchases. However, it does not explicitly define whether `Buyer` must inherit from `User` or be represented as a separate profile associated with a User.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| primaryAddress | `String` | Main address normally used for deliveries. | Explicit |
| additionalAddresses | `List<String>` | Optional secondary delivery addresses. | Explicit |
| commercialStatus | `BuyerStatus` | Commercial condition that determines the Buyer's participation in purchasing processes. | Explicit concept; type inferred |

## Relationships

- A Buyer uses a `Cart` to select products.
- A Buyer creates or confirms `Order` instances.
- A Buyer receives physical products through `Shipment` processes.
- A Buyer may participate in `Return` and `Refund` processes.
- `Buyer` inherits from `User` and always returns the `BUYER` role.

## Business Rules

- A Buyer may register themselves in NexusMarket.
- A Buyer must have a primary delivery address.
- Additional delivery addresses are optional.
- A Buyer must have a commercial status.
- A Buyer must not manage information belonging to other Buyers.
- A Buyer must not manage inventory.
- A Buyer may only interact with information permitted by their assigned role.

## Design Notes

- `Buyer` is implemented as a User Entity specialization with primary address, additional addresses, and commercial status.
- `BuyerStatus` is implemented with `ACTIVE` and `SUSPENDED`; both values are documented as domain inferences from commercial participation.
- The specification does not define whether a Buyer has one active Cart or multiple Carts.
- The inheritance or association between `Buyer` and `User` must be determined after analyzing the remaining participants.

---

# Seller

## Description

Represents a provider responsible for offering and commercializing products through NexusMarket.

A Seller registers and manages products, participates in inventory administration, and manages orders related to their commercial operation.

The Seller cannot register themselves. Their incorporation into NexusMarket must be performed by an Administrator.

## Attributes

The functional specification does not explicitly define Seller-specific attributes.

General identification, name, email, role, and status information are inherited from `User`.

## Relationships

- A Seller registers and manages `Product` instances.
- A Seller may have one or more associated `Warehouse` instances.
- A Seller participates in the administration of `Inventory`.
- A Seller participates in the management of `Order` instances.
- An Administrator registers the Seller and their first Warehouse.
- `Seller` inherits from `User` and always returns the `SELLER` role.

## Business Rules

- A Seller must not self-register.
- A Seller must be registered by an Administrator.
- The Administrator registers the Seller and their first Warehouse during incorporation.
- A Seller may only register and manage their own products.
- A Seller may participate in inventory administration.
- A Seller may only interact with information permitted by their assigned role.

## Design Notes

- `Seller` is implemented as a User Entity specialization that manages Product and Warehouse collections.
- The specification does not define specific Seller attributes such as registration date, business name, or seller identifier.
- Attributes not supported by the specification must not be added without confirmation.
- The inheritance or association between `Seller` and `User` will be determined after completing the participant analysis.

---

# Warehouse

## Description

Represents a physical storage location where product inventory is managed within NexusMarket.

The functional specification distinguishes between Warehouses owned by the Marketplace and Warehouses associated with Sellers.

Warehouses participate in inventory storage, order preparation, dispatch, and other physical logistics operations.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| ownerType | `WarehouseOwnerType` | Indicates whether the Warehouse belongs to the Marketplace or to a Seller. | Explicit classification; attribute inferred |

The functional specification does not explicitly define other Warehouse attributes such as identifier, name, capacity, or address.

## Relationships

- A Warehouse belongs either to the Marketplace or to a Seller.
- A Seller may have one or more associated Warehouses.
- A Warehouse contains or manages `Inventory` records.
- Every Inventory record must be associated with a specific Warehouse.
- A Logistics Operator participates in the physical operation of Warehouses.
- An Administrator registers the Seller's first Warehouse during Seller incorporation.
- A Warehouse may participate in the preparation and dispatch of physical orders.

## Business Rules

- Every Warehouse must have an owner classification.
- A Warehouse must be classified as belonging to the Marketplace or to a Seller.
- A Seller Warehouse must be associated with a Seller.
- Inventory must not exist without an associated Warehouse.
- Warehouse information may only be managed by participants authorized by their role.
- The Seller and Logistics Operator may participate in inventory administration.

## Design Notes

- `Warehouse` is considered a candidate Entity because it represents a distinguishable physical business location and maintains relationships with inventory and logistics operations.
- `WarehouseOwnerType` is a candidate Value Object.
- Its proposed values are `MARKETPLACE` and `SELLER`, directly supported by the Warehouse classification in the specification.
- Attributes such as address and capacity must not be added until they are supported by the specification or confirmed by the professor.
- The specification does not define the exact number of Warehouses that a Seller may have.

---

# Product

## Description

Represents a good offered and commercialized by a Seller through the NexusMarket catalog.

The catalog distinguishes between physical and digital Products. Physical Products require inventory management and dispatch, while digital Products may be delivered immediately after payment confirmation.

A Product may contain variants representing differences such as color, size, or model.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| productType | `ProductType` | Indicates whether the Product is physical or digital. | Explicit |
| variants | `List<ProductVariant>` | Variations of the Product, such as color, size, or model. | Explicit concept; class inferred |
| status | `ProductStatus` | Current commercial condition of the Product in the catalog. | Explicit |

The functional specification does not explicitly define other attributes such as product identifier, name, description, price, image, or category.

## Relationships

- A Product is registered and managed by a Seller.
- A Seller may register and manage multiple Products.
- A physical Product may have Inventory distributed across one or more Warehouses.
- Every Inventory record must reference a specific Product.
- A Product may have multiple Product Variants.
- A Product may be selected through a Cart.
- A Product may participate in an Order through an Order Item.

## Business Rules

- Only a Seller may register Products.
- A Seller may only manage their own Products.
- Every Product must have a Product Type.
- Every Product must have a Product Status.
- A physical Product requires inventory and dispatch.
- A digital Product may be delivered immediately after payment confirmation.
- A Product with `PUBLISHED` status is visible in the public catalog.
- Product status must use one of the values defined by the domain.

## Product Type

The functional specification defines the following Product Types:

- `PHYSICAL`
- `DIGITAL`

## Product Status

The functional specification defines the following Product Status values:

- `PUBLISHED`
- `SUSPENDED`
- `DISCONTINUED`

## Design Notes

- `Product` is a candidate Entity because it participates in the catalog, inventory, Cart, Order, and commercial lifecycle.
- `ProductType` is a candidate Value Object.
- `ProductStatus` is a candidate Value Object.
- `ProductVariant` is implemented as an immutable Value Object whose required description may represent color, size, model, or a combination of characteristics.
- Attributes such as name, description, price, category, or product identifier must not be added without further support or confirmation.

---

# Inventory

## Description

Represents the stock of a specific Product stored in a specific Warehouse.

NexusMarket manages distributed Inventory. This means that the stock of the same Product may be located in different Warehouses.

Every Inventory record must be obligatorily associated with one Product and one specific Warehouse.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| product | `Product` | Product whose stock is being managed. | Explicit relationship |
| warehouse | `Warehouse` | Specific Warehouse where the stock is located. | Explicit relationship |
| availableQuantity | `Integer` | Quantity currently available for commercial operations. | Inferred from stock management |
| condition | `InventoryCondition` | Operational condition used to identify unavailable or damaged Inventory. | Inferred from critical validation |

The functional specification does not explicitly define separate quantities for available, reserved, damaged, or total stock.

## Relationships

- Every Inventory record references exactly one Product.
- Every Inventory record references exactly one Warehouse.
- A Product may have Inventory distributed across multiple Warehouses.
- A Warehouse may contain Inventory for multiple Products.
- Inventory changes may generate Inventory Movement records.
- Sellers and Logistics Operators participate in Inventory administration.

## Business Rules

- Inventory must always be associated with a Product.
- Inventory must always be associated with a specific Warehouse.
- Inventory quantities must never be negative.
- Inventory must not be reserved when the requested stock does not exist.
- Inventory marked as damaged must not be reserved.
- Physical Products require Inventory management.
- Inventory may only be managed by participants authorized by their role.
- Every Inventory change must correspond to an allowed Inventory Movement Type.

## Inventory Movement Types

The functional specification defines the following movements:

- `ENTRY`
- `RESERVATION`
- `SALE_EXIT`
- `ADJUSTMENT`
- `RETURN`

## Design Notes

- `Inventory` is a candidate Entity because it maintains operational stock for a Product in a specific Warehouse.
- `InventoryMovementType` is implemented as a controlled enum.
- `InventoryCondition` is implemented with `AVAILABLE` and `DAMAGED`. `DAMAGED` is explicit and `AVAILABLE` is the inferred normal reservable condition.
- The only condition explicitly mentioned by the specification is `DAMAGED`; the remaining allowed conditions are not defined.
- `InventoryMovement` is implemented as an immutable traceability model containing Inventory, movement type, positive quantity, and the authorized User who performed it.
- The specification does not define whether the combination of Product and Warehouse must be unique.
- Separate fields such as `reservedQuantity`, `damagedQuantity`, and `totalQuantity` must not be added without further analysis or confirmation.

---

# Cart

## Description

Represents the provisional selection of Products made by a Buyer before confirming a formal Order.

The Cart allows the Buyer to select Products during the purchasing process. Once the Buyer confirms the purchase, the system begins the formal Order lifecycle and waits for payment confirmation.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| buyer | `Buyer` | Buyer who owns and manages the Cart. | Inferred from purchasing flow |
| items | `List<CartItem>` | Products provisionally selected by the Buyer. | Supporting structure inferred |

The functional specification does not explicitly define Cart attributes such as identifier, creation date, expiration date, total amount, or status.

## Relationships

- A Cart belongs to a Buyer.
- A Buyer uses the Cart to select Products.
- A Cart contains Products through Cart Item records.
- A confirmed Cart initiates the formal Order process.
- A Cart may contain physical and digital Products.
- `Cart` is an independent Entity. Confirming a non-empty Cart for an authorized Buyer creates an `Order` in `PENDING_PAYMENT` status.

## Business Rules

- A Cart represents a provisional Product selection.
- A Buyer may only manage their own Cart.
- The Cart does not represent a finalized commercial commitment.
- Confirming the Cart initiates the purchasing and payment process.
- Cart information may only be accessed according to the Buyer's role.
- Products selected in the Cart must use information from the NexusMarket catalog.

## Cart Item

`CartItem` is a candidate supporting class used to represent each Product selected in a Cart.

Possible required information includes:

| Attribute | Type | Description | Source |
|---|---|---|---|
| product | `Product` | Product selected by the Buyer. | Inferred |
| quantity | `Integer` | Quantity requested by the Buyer. | Inferred |

The complete Cart Item structure is not defined by the functional specification.

## Design Notes

- `Cart` is implemented as an Entity that belongs to one Buyer and contains mutable `CartItem` models.
- `CartItem` is classified as a mutable internal model because its quantity may change while the selection is provisional.
- The specification presents Cart both as a separate managed process and as the first stage of the Order lifecycle.
- It must be determined whether Cart is a separate Entity that produces an Order or whether it is represented directly as an initial Order state.
- The specification does not define whether a Buyer may have one active Cart or multiple Carts.
- Attributes such as price, subtotal, selected variant, or reservation expiration must not be added without further support.

---

# Order

## Description

Represents the formal commercial commitment created when a Buyer confirms a purchase in NexusMarket.

The Order lifecycle is the central business process of the Marketplace. It begins with the provisional Product selection, continues through payment and logistics, and finishes after delivery confirmation.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| buyer | `Buyer` | Buyer responsible for the purchase. | Inferred from purchasing flow |
| items | `List<OrderItem>` | Products and quantities included in the Order. | Supporting structure inferred |
| status | `OrderStatus` | Current lifecycle state of the Order. | Explicit |

The specification does not explicitly define attributes such as order identifier, creation date, total amount, payment reference, or delivery date.

## Relationships

- An Order is created or confirmed by a Buyer.
- An Order contains Products through Order Item records.
- Sellers participate in the management of Orders involving their Products.
- Logistics Operators participate in Order preparation and dispatch.
- A paid Order may generate billing information.
- A physical Order participates in a Shipment process.
- A delivered Order may participate in Return and Refund processes.
- The implemented Order constructor copies Cart items into immutable OrderItems and leaves later Cart changes isolated from the formal Order.

## Business Rules

- An Order represents a formal commercial commitment.
- Every Order operation must be performed by an authenticated User.
- A Buyer may only manage their own purchasing information.
- Sellers and Logistics Operators may only manage Order information authorized by their roles.
- Payment confirmation initiates the preparation process.
- A finalized Order must not be modified under any circumstance.
- Order status changes must follow the lifecycle defined by the functional specification.
- Physical Products require logistics and dispatch.
- Digital Products may be delivered immediately after payment confirmation.

## Order Lifecycle

The functional specification defines the following sequence:

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

### CART

Represents a provisional selection of Products before the purchase becomes a formal commitment.

### PENDING_PAYMENT

The purchase is waiting for financial confirmation.

### PAID

Payment has been confirmed and the preparation process begins.

### DISPATCHED

The physical Order has left the Warehouse.

### DELIVERED / FINALIZED

The delivery has been successfully completed.

The specification does not clearly establish whether `DELIVERED` and `FINALIZED` are two separate states or two names for the same final state.

## Order Item

`OrderItem` is a candidate supporting class used to represent every Product included in an Order.

Possible required information includes:

| Attribute | Type | Description | Source |
|---|---|---|---|
| product | `Product` | Product included in the Order. | Inferred |
| quantity | `Integer` | Quantity purchased. | Inferred |

The functional specification does not explicitly define Order Item attributes such as unit price, subtotal, selected variant, or assigned Warehouse.

## Design Notes

- `Order` is implemented as an Entity with a controlled lifecycle and without a generic status setter.
- `OrderStatus` is implemented with `PENDING_PAYMENT`, `PAID`, `DISPATCHED`, `DELIVERED`, and `FINALIZED`.
- `OrderItem` is classified as an immutable internal model of Order.
- The document presents `CART` as an Order state while also defining Cart management as an independent process.
- The Order lifecycle for digital Products requires further analysis because digital delivery may occur immediately after payment.
- The specification does not define whether one Order may contain Products from multiple Sellers or Warehouses.

---

# Invoice

## Description

Represents the commercial billing information associated with a purchase made through NexusMarket.

The functional specification includes billing management as part of the Marketplace operation. However, it does not define the complete internal structure, lifecycle, or status values of an Invoice.

Therefore, `Invoice` is implemented as a Domain Entity associated with an Order after payment confirmation.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| order | `Order` | Order associated with the billing information. | Relationship inferred from the purchasing process |

The specification does not explicitly define other Invoice attributes such as identifier, issue date, invoice number, total amount, tax information, Buyer information, Seller information, or status.

## Relationships

- Billing information is associated with a purchase.
- An Invoice may be associated with an Order.
- An Order may generate billing information as part of the purchasing process.
- Billing information may involve the Buyer and Sellers participating in the Order.
- The exact cardinality between Order and Invoice is not defined.

## Business Rules

- Billing information must correspond to a purchase processed by NexusMarket.
- Invoice information may only be accessed or managed by Users authorized by their role.
- Billing information must maintain a relationship with the commercial operation that produced it.
- The Invoice lifecycle must not be invented until the required billing states are defined.

## Design Notes

- `Invoice` is an implemented Entity inferred from the billing management process.
- The specification uses the business concept of billing but does not explicitly define an Invoice class.
- No `InvoiceStatus` Value Object will be defined until its allowed values are supported or confirmed.
- It must be confirmed whether an Order generates one Invoice or multiple Invoices.
- It must also be confirmed how billing operates when an Order contains Products from multiple Sellers.
- Financial calculations, taxes, invoice numbering, and legal billing requirements are not defined by the functional specification.

---

# Shipment

## Description

Represents the logistics process used to prepare, dispatch, transport, and deliver physical Products associated with an Order.

The functional specification establishes that physical Products require inventory and dispatch. Logistics Operators are responsible for the physical operation of Warehouses and dispatches.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| order | `Order` | Order whose physical Products are being shipped. | Relationship inferred from the logistics flow |
| originWarehouse | `Warehouse` | Warehouse participating in preparation and dispatch. | Relationship inferred from the logistics flow |

The specification does not explicitly define Shipment attributes such as identifier, carrier, tracking number, dispatch date, delivery date, destination address, or status.

## Relationships

- A Shipment is associated with a physical Order.
- A Shipment may originate from a Warehouse.
- A Logistics Operator participates in preparation and dispatch.
- A Buyer receives the delivery associated with a Shipment.
- A delivered Shipment participates in the closing of an Order.
- The number of Shipments associated with one Order is not defined.

## Business Rules

- Physical Products require a logistics and dispatch process.
- Shipment operations must be performed by Users authorized by their role.
- Dispatch occurs after payment confirmation and preparation.
- Order closure occurs after delivery confirmation.
- Digital Products may be delivered immediately after payment and do not require physical dispatch.

## Design Notes

- `Shipment` is an implemented Entity for the dispatch and delivery behavior of paid physical Orders.
- The specification does not define a Shipment lifecycle or allowed Shipment statuses.
- No `ShipmentStatus` Value Object will be defined until its values are supported or confirmed.
- It must be confirmed whether one Order may produce multiple Shipments when Products are stored in different Warehouses.

---

# Return

## Description

Represents the post-sale process through which a Buyer returns Products associated with a completed purchase.

The functional specification includes Return management within the NexusMarket scope but does not define its attributes, lifecycle, authorization rules, or acceptance conditions.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| order | `Order` | Order associated with the returned Product or Products. | Relationship inferred from the post-sale process |
| buyer | `Buyer` | Buyer participating in the Return process. | Relationship inferred from the post-sale process |

The specification does not explicitly define attributes such as return identifier, reason, request date, returned Products, quantities, evidence, or status.

## Relationships

- A Return is associated with an Order.
- A Buyer may participate in a Return process.
- A Return may involve one or more Products from an Order.
- An accepted Return may participate in a Refund process.
- The participants responsible for approving or rejecting Returns are not defined.

## Business Rules

- Return management belongs to the post-sale scope of NexusMarket.
- Return information may only be accessed or managed by Users authorized by their role.
- Return acceptance rules must not be invented because they are not defined by the specification.
- A Return lifecycle must not be established until its allowed states are confirmed.

## Design Notes

- `Return` is an implemented Entity related to a delivered or finalized Order and its Buyer.
- No `ReturnStatus` Value Object will be defined until its allowed values are supported or confirmed.
- Return deadlines, eligible Product conditions, required evidence, and approval responsibilities are not specified.

---

# Refund

## Description

Represents the post-sale commercial process through which money associated with a purchase may be returned to a Buyer.

The functional specification includes Refund management and assigns participation in this process to Buyers and Administrators. It does not define the complete Refund structure, lifecycle, calculation rules, or payment mechanism.

## Attributes

| Attribute | Type | Description | Source |
|---|---|---|---|
| buyer | `Buyer` | Buyer participating in the Refund process. | Explicit responsibility; relationship inferred |
| order | `Order` | Order associated with the commercial operation. | Relationship inferred from the post-sale process |
| returnRequest | `Return` | Return process that may originate the Refund. | Relationship inferred |

The specification does not explicitly define attributes such as refund identifier, amount, reason, processing date, payment method, or status.

## Relationships

- A Buyer participates in Refund management.
- An Administrator participates in Refund management.
- A Refund may be associated with an Order.
- A Refund may result from an accepted Return.
- The exact relationship and cardinality between Return and Refund are not defined.

## Business Rules

- Refund information may only be managed by participants authorized by their role.
- Buyers and Administrators participate in Refund management.
- Refund calculation and approval rules must not be invented.
- The Refund lifecycle must not be established until its states are confirmed.
- Technical payment mechanisms are outside the functional specification.

## Design Notes

- `Refund` is an implemented Entity associated with a Return and processed by an active Administrator.
- No `RefundStatus` Value Object will be defined until its values are supported or confirmed.
- Refund amounts, partial Refunds, processing deadlines, and financial mechanisms are not specified.

---

# Supporting Domain Concepts

# Product Variant

## Description

Represents a variation of a Product based on characteristics such as color, size, or model.

The specification defines Product variants as a list but does not define their attributes, identity, combination rules, price differences, or inventory behavior.

## Design Notes

- `ProductVariant` is an implemented immutable Value Object.
- Equality is based on its normalized description.
- Variant attributes must not be invented until the required structure is confirmed.
- It must be confirmed whether Inventory is managed per Product or per Product Variant.

# Inventory Movement

## Description

Represents a change applied to Inventory.

The specification explicitly defines the following movement types:

- `ENTRY`
- `RESERVATION`
- `SALE_EXIT`
- `ADJUSTMENT`
- `RETURN`

## Design Notes

- `InventoryMovement` is an implemented supporting domain model for operational traceability.
- `InventoryMovementType` is an implemented Value Object enum with values explicitly supported by the specification.
- Movement attributes such as quantity, date, performed User, reason, and resulting stock are not defined.

# Administrative Report

## Description

Represents the administrative consultation capability required to consolidate Marketplace information.

## Design Notes

- `AdministrativeReport` is not automatically considered an Entity.
- It may be implemented later as a query, application service, projection, or response model.
- The functional specification does not define report types, filters, formats, or stored report records.

---

# Global Domain Relationships

```text
User (abstract)
  |-- has one --> SystemRole
  |-- has one --> UserStatus
  |-- specialized by --> Buyer
  |-- specialized by --> Seller
  |-- specialized by --> LogisticsOperator
  |-- specialized by --> Administrator
  `-- specialized by --> Supervisor

Buyer
  |-- uses --> Cart
  |-- confirms --> Order
  |-- receives --> Shipment
  |-- participates in --> Return
  `-- participates in --> Refund

Seller
  |-- manages --> Product
  |-- associates with --> Warehouse
  |-- administers --> Inventory
  `-- participates in --> Order

Product
  |-- belongs to or is managed by --> Seller
  |-- may contain --> ProductVariant
  |-- is stored through --> Inventory
  |-- is selected through --> CartItem
  `-- is purchased through --> OrderItem

Inventory
  |-- references one --> Product
  |-- references one --> Warehouse
  `-- changes through --> InventoryMovement

Cart
  |-- belongs to --> Buyer
  |-- contains --> CartItem
  `-- initiates --> Order

Order
  |-- belongs to --> Buyer
  |-- contains --> OrderItem
  |-- may generate --> Invoice
  |-- may generate --> Shipment
  |-- may participate in --> Return
  `-- may participate in --> Refund
```

This relationship structure is implemented for the first delivery. Cardinalities that require identifiers, persistence design, or additional commercial attributes remain open.

---

# Implemented Value Objects

The first delivery implements the following controlled business concepts. Explicit and inferred values are detailed in `Domain Value Objects.md`.

| Value Object | Implemented Values or Structure |
|---|---|
| `SystemRole` | `BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR` |
| `UserStatus` | `ACTIVE`, `INACTIVE`, `BLOCKED` |
| `BuyerStatus` | `ACTIVE`, `SUSPENDED` |
| `WarehouseOwnerType` | `MARKETPLACE`, `SELLER` |
| `ProductType` | `PHYSICAL`, `DIGITAL` |
| `ProductStatus` | `PUBLISHED`, `SUSPENDED`, `DISCONTINUED` |
| `ProductVariant` | Immutable description-based Value Object |
| `InventoryMovementType` | `ENTRY`, `RESERVATION`, `SALE_EXIT`, `ADJUSTMENT`, `RETURN` |
| `InventoryCondition` | `AVAILABLE`, `DAMAGED` |
| `OrderStatus` | `PENDING_PAYMENT`, `PAID`, `DISPATCHED`, `DELIVERED`, `FINALIZED` |

`InvoiceStatus`, `ShipmentStatus`, `ReturnStatus`, and `RefundStatus` are deliberately not implemented because their allowed values are not defined.

---

# Implementation Status

The following domain models are implemented in plain Java:

- `User`, `Buyer`, `Seller`, `LogisticsOperator`, `Administrator`, and `Supervisor`.
- `Warehouse`, `Product`, `Inventory`, and `InventoryMovement`.
- `Cart`, `CartItem`, `Order`, and `OrderItem`.
- `Invoice`, `Shipment`, `Return`, and `Refund`.

The implementation has automated unit coverage for entity creation, required data, authorization, Inventory constraints, Cart behavior, Order transitions, billing eligibility, physical Shipment flow, Return eligibility, and Refund processing.

At the current delivery checkpoint, Maven executes 102 tests with zero failures and zero errors. One generated Spring application-context test is temporarily skipped until database configuration is introduced.

---

# Global Business Rules

- Every operation must be executed by an authenticated User.
- Every User has exactly one role.
- No participant may manage information outside the responsibilities of their role.
- User identification must be unique in NexusMarket.
- User email must be unique in NexusMarket.
- A Buyer may register themselves.
- A Buyer must not manage other Buyers or Inventory.
- A Seller must not self-register.
- A Seller must be registered by an Administrator.
- The Administrator registers the Seller and their first Warehouse.
- Only a Seller registers and manages their own Products.
- Inventory must reference a Product and a specific Warehouse.
- Inventory quantities must never be negative.
- Nonexistent or damaged Inventory must not be reserved.
- Physical Products require Inventory and dispatch.
- Digital Products may be delivered immediately after payment confirmation.
- A finalized Order must not be modified under any circumstance.

---

# Remaining Open Decisions

The first implementation resolves the inheritance, Cart, Order lifecycle, supporting-item classification, and operational status decisions. The following questions remain because the specification provides no sufficient basis for a definitive choice:

1. Whether one Order may contain Products from multiple Sellers.
2. Whether one Order may contain Products from multiple Warehouses.
3. Whether one Order may generate multiple Invoices or Shipments.
4. Whether Inventory will eventually be managed per Product or per ProductVariant.
5. Additional attributes for Product, Invoice, Shipment, Return, and Refund.
6. Independent status catalogs for Invoice, Shipment, Return, and Refund.
7. Exact persistence identifiers and cardinalities not explicitly defined by the specification.

These unresolved points are not silently implemented. Future requirements must update this model before the corresponding behavior is added.

---

# Domain Design Rules

- The functional specification remains the primary source of truth.
- Explicit requirements, teacher clarifications, technical inferences, and pending decisions must remain distinguishable.
- The banking repository provides structural guidance but does not define NexusMarket requirements.
- Domain classes must represent real NexusMarket business concepts.
- Inheritance must only represent genuine domain specialization.
- Controlled roles, statuses, and types must not be represented by arbitrary Strings.
- Business rules must remain in the Domain and must not be delegated to controllers or persistence technology.
- Technical authentication, database design, API contracts, and infrastructure details are outside this Domain Model stage.
