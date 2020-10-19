package guru.springframework.brewery.web.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;


@WebMvcTest(BeerController.class)
public class BeerControllerTest {

	@MockBean
	BeerService beerService;
	
	@Autowired
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
	}
	
	@AfterEach
	void tearDown() {
		reset(beerService);
	}
	
	@Test
	void testGetBeerById() throws Exception {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		
		when(beerService.findBeerById(any())).thenReturn(validBeer);
		
		mockMvc.perform(get("/api/v1/beer/" + validBeer.getId()))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
		.andExpect(jsonPath("$.beerName", is("Beer1")))
		.andExpect(jsonPath("$.createdDate", is(dateTimeFormatter.format(validBeer.getCreatedDate()))));
	}
	
	@DisplayName("List Ops - ")
	@Nested
	public class TestListOperations {
		
		@Captor
		ArgumentCaptor<String> beerNameCaptor;
		
		@Captor
		ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;
		
		@Captor
		ArgumentCaptor<PageRequest> pageRequestCaptor;
		
		BeerPagedList beerPageList;
		
		@BeforeEach
		void setUp() {
			List<BeerDto> beers = new ArrayList<BeerDto>();
			beers.add(validBeer);
			beers.add(BeerDto.builder().id(UUID.randomUUID())
				.version(1)
				.upc(123123123L)
				.beerName("Beer4")
				.beerStyle(BeerStyleEnum.PALE_ALE)
				.price(new BigDecimal("12.99"))
				.quantityOnHand(66)
				.createdDate(OffsetDateTime.now())
				.lastModifiedDate(OffsetDateTime.now())
				.build());
			
			beerPageList = new BeerPagedList(beers, PageRequest.of(1, 1), 2);
			
			when(beerService.listBeers(beerNameCaptor.capture(), beerStyleEnumCaptor.capture(), pageRequestCaptor.capture()))
				.thenReturn(beerPageList);
		}
		
		@DisplayName("Test list beers - no parameters")
		@Test
		void testListBeers() throws Exception {
			mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].id", is(validBeer.getId().toString())));
		}		
	}	
}
