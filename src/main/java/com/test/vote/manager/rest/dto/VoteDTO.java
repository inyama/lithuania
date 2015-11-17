package com.test.vote.manager.rest.dto;

public class VoteDTO  implements DTO {
    private int id;
    private long voteDate;
    private int userId;
    private int restaurantId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(long voteDate) {
        this.voteDate = voteDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
