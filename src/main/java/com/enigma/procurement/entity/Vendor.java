package com.enigma.procurement.entity;

import com.enigma.procurement.entity.constraint.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "m_vendor")
@EntityListeners(AuditingEntityListener.class)

public class Vendor extends Auditable<String> {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    private String name;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(targetEntity = Address.class ,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Address address;

    @OneToOne(targetEntity = UserCredential.class)
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

}
