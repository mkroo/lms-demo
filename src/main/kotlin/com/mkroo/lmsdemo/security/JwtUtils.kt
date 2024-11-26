package com.mkroo.lmsdemo.security

import com.mkroo.lmsdemo.domain.Account
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.time.Duration

class JwtUtils(
    secret: String,
    private val expiresIn: Duration
) {
    companion object {
        private val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256
    }

    private val signingKey = getSigningKey(secret)
    private val parser = Jwts.parserBuilder().setSigningKey(signingKey).build()

    fun issue(account: Account) : String {
        return Jwts.builder()
            .setClaims(AccountJwtClaims.of(account).toClaims(expiresIn))
            .signWith(signingKey, SIGNATURE_ALGORITHM)
            .compact()
    }

    fun parse(token: String) : AccountJwtClaims {
        val claims = parser.parseClaimsJws(token).body

        return AccountJwtClaims.of(claims)
    }

    private fun getSigningKey(keyString: String) : Key {
        val keyBytes = Decoders.BASE64.decode(keyString)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
