package leh.example.person;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import leh.annotations.Identity;
import leh.example.food.Food;
import leh.util.Entity;

public class Person implements Entity {

	enum Gender {MALE, FEMALE, UNKNOWN};
	
	@Identity
	private String ssn;
	
	private Color hairColor;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private List<Food> favoriteFoods;
	private Gender gender = Gender.UNKNOWN;
	private long netWorth;
	private Person spouse;

	public Color getHairColor() {
		return hairColor;
	}

	public void setHairColor(Color hairColor) {
		this.hairColor = hairColor;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public List<Food> getFavoriteFoods() {
		return favoriteFoods;
	}

	public void setFavoriteFoods(List<Food> favoriteFoods) {
		this.favoriteFoods = favoriteFoods;
	}
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public long getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(long netWorth) {
		this.netWorth = netWorth;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public Person getSpouse() {
		return spouse;
	}

	public void setSpouse(Person spouse) {
		this.spouse = spouse;
	}
	
}
