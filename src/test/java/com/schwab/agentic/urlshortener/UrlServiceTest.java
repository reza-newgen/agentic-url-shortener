package com.schwab.agentic.urlshortener;
import com.schwab.agentic.urlshortener.service.*;import com.schwab.agentic.urlshortener.repository.*;
import org.junit.jupiter.api.*;import org.mockito.*;import org.springframework.data.redis.core.*;import static org.junit.jupiter.api.Assertions.*;
class UrlServiceTest {
 @Test void rejectUnsupportedSchemes(){UrlService s=new UrlService(Mockito.mock(UrlRepository.class),Mockito.mock(StringRedisTemplate.class));assertThrows(IllegalArgumentException.class,()->s.create("javascript:alert(1)",null));}
}
