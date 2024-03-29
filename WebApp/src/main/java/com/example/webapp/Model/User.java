package com.example.webapp.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="user")
public class User {

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@JsonProperty(required = true)
	@Column(name = "first_name")
	private String first_name;
	
	@NotNull
	@JsonProperty(required = true)
	@Column(name = "last_name")
	private String last_name;
	
	@NotNull
	@Column(name = "username")
	private String username;
	
	@NotNull
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "password")
	private String password;
	
	@NotNull
	@JsonProperty(access = Access.READ_ONLY)
	@Column(name = "account_created")
	private String account_created;
	
	@NotNull
	@JsonProperty(access = Access.READ_ONLY)
	@Column(name = "account_updated")
	private String account_updated;

	@NotNull
	@JsonProperty(access = Access.READ_ONLY)
	@Column(name = "is_Verified")
	private boolean is_Verified;

	public boolean isIs_Verified() {return is_Verified;}

	public void setIs_Verified(boolean is_Verified) {this.is_Verified = is_Verified;}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccount_created() {
		return account_created;
	}

	public void setAccount_created(String account_created) {
		this.account_created = account_created;
	}

	public String getAccount_updated() {
		return account_updated;
	}

	public void setAccount_updated(String account_updated) {
		this.account_updated = account_updated;
	}

	
	@Override
	public String toString() {
		return "User [id=" + id + ", first_name=" + first_name + ", last_name=" + last_name + ", username=" + username
				+ ", password=" + password + ", account_created=" + account_created + ", account_updated="
				+ account_updated + "]";
	}
	
	public User() {
		
	}

	public User(String first_name, String last_name, String username, String password, String account_created,
			String account_updated,boolean is_Verified) {
		super();
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.password = password;
		this.account_created = account_created;
		this.account_updated = account_updated;
		this.is_Verified=false;
	}
	
	
	
	

}
