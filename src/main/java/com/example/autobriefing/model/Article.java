package com.example.autobriefing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    private String id;
    private String title;

    @Column(length = 10000)
    private String content;
    private String imageUrl;
    private Date date;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // All-args except createdAt/updatedAt
    public Article(String id, String title, String content, String imageUrl, Date date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.date = date;
    }

}
