package application.adapters.output.persistence.jpa.entities;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA utilizada para almacenar productos.
 */
@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    @Column(
            name = "product_id",
            nullable = false,
            length = 36
    )
    private String productId;

    /**
     * Identificación del vendedor propietario.
     *
     * Se conserva como identificación para evitar acoplar
     * esta entidad a la implementación JPA de usuarios.
     */
    @Column(
            name = "seller_identification",
            length = 100
    )
    private String sellerIdentification;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "product_type",
            nullable = false,
            length = 20
    )
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private ProductStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "product_variants",
            joinColumns = @JoinColumn(
                    name = "product_id"
            )
    )
    @Column(
            name = "description",
            nullable = false
    )
    private List<String> variants =
            new ArrayList<>();

    /**
     * Constructor requerido por JPA.
     */
    protected ProductJpaEntity() {
    }

    /**
     * Constructor conservado para compatibilidad
     * con las pruebas anteriores.
     */
    public ProductJpaEntity(
            String productId,
            ProductType productType,
            ProductStatus status,
            List<String> variants
    ) {
        this(
                productId,
                null,
                productType,
                status,
                variants
        );
    }

    /**
     * Construye una entidad con toda la información.
     */
    public ProductJpaEntity(
            String productId,
            String sellerIdentification,
            ProductType productType,
            ProductStatus status,
            List<String> variants
    ) {
        this.productId = productId;
        this.sellerIdentification =
                sellerIdentification;
        this.productType = productType;
        this.status = status;
        setVariants(variants);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerIdentification() {
        return sellerIdentification;
    }

    public void setSellerIdentification(
            String sellerIdentification
    ) {
        this.sellerIdentification =
                sellerIdentification;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(
            ProductType productType
    ) {
        this.productType = productType;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<String> getVariants() {
        return List.copyOf(variants);
    }

    public void setVariants(List<String> variants) {
        this.variants = variants == null
                ? new ArrayList<>()
                : new ArrayList<>(variants);
    }
}