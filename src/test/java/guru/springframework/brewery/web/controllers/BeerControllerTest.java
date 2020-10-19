package guru.springframework.brewery.web.controllers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerStyleEnum;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;



@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

	@Mock
	BeerService beerService;
	
	@InjectMocks
	BeerController beerController;
	
	MockMvc mockMvc;
	
	BeerDto validBeer;
	
	@BeforeEach
	void setUp() {
		validBeer = BeerDto.builder().id(UUID.randomUUID())
				.version(1)
				.beerName("Beer1")
				.beerStyle(BeerStyleEnum.PALE_ALE)
				.price(new BigDecimal("12.99"))
				.quantityOnHand(4)
				.createdDate(OffsetDateTime.now())
				.lastModifiedDate(OffsetDateTime.now())
				.build();
		
		mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
	}
	
	@Test
	void testGetBeerById() throws Exception {
		when(beerService.findBeerById(any())).thenReturn(validBeer);
		
		mockMvc.perform(get("/api/v1/beer/" + validBeer.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
		.andExpect(jsonPath("$.beerName", is("Beer1")));
	}
	
	
}
