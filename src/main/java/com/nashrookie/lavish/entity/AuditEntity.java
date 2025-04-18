package com.nashrookie.lavish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditEntity{
  @CreatedBy
  @Column(name = "created_by", length = 255)
  protected String createdBy;

  @CreationTimestamp
  @Column(name = "created_on")
  protected ZonedDateTime createdOn;

  @LastModifiedBy
  @Column(name = "last_modified_by", length = 255)
  protected String lastModifiedBy;

  @UpdateTimestamp
  @Column(name = "last_modified_on")
  protected ZonedDateTime lastModifiedOn;
}