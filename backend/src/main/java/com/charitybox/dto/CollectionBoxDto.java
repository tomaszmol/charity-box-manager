package com.charitybox.dto;

public class CollectionBoxDto {
    private Long id;
    private boolean assigned;
    private boolean empty;

    public CollectionBoxDto(Long id, boolean assigned, boolean empty){
        this.id = id;
        this.assigned = assigned;
        this.empty = empty;
    }

    public Long getId() { return id; }
    public boolean isAssigned() { return assigned; }
    public boolean isEmpty() { return empty; }
}
