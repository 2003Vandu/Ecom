package com.eComm.eComm.io;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest
{
    private String name;
    @JsonAlias({"description", "Description"})
    private String description;
    private String bgColor;
}
