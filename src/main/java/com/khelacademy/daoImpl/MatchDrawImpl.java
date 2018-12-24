package com.khelacademy.daoImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.khelacademy.dao.MatchDraw;
import com.khelacademy.www.pojos.*;
import com.khelacademy.www.services.ServiceUtil;
import com.khelacademy.www.utils.DBArrow;
import com.khelacademy.www.utils.ByesContants;
import com.khelacademy.www.utils.EnglishNumberToWords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

public class MatchDrawImpl implements MatchDraw {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    static int counterForPools = 1;
    DBArrow SQLArrow = DBArrow.getArrow();
	@Override
	public Map<String, List<User>> groupPlayers(Integer eventId) {
		Integer i = null;
        Map<String, List<User>> groupByCategotyMap = new HashMap<String, List<User>>();
        PreparedStatement statement=null;
        try {
            if(eventId != null){
                statement = SQLArrow.getPreparedStatement("select * from temp_users as t inner join booking as b on t.booking_id = b.booking_id inner join ticket as tk on b.booking_id = tk.booking_id inner join price as p on t.price_id = p.price_id and b.status = ? AND tk.event_id =?");
                statement.setInt(2, eventId);
                statement.setString(1, "completed");
            }else{
                LOGGER.error("ERROR IN PREPARING STATEMENT FOR EVENT BASED USER FOR THE EVENTID : " + eventId);
            }
            try (ResultSet rs = SQLArrow.fire(statement)) {
                while (rs.next()) {
                    User u = new User();
                    u.setFirstName(rs.getString("NAME"));
                    u.setUserId(rs.getInt("USER_ID"));
                    u.setGameId(rs.getInt("game_user_id"));
                    u.setUnderX(rs.getString("price_name"));
                    if(groupByCategotyMap.get(String.valueOf(rs.getInt("category_id"))) == null){
                        groupByCategotyMap.put(String.valueOf(rs.getInt("category_id")), new ArrayList<User>());
                    }
                    groupByCategotyMap.get(String.valueOf(rs.getInt("category_id"))).add(u);
                }
            }catch(Exception e){
                LOGGER.error("ERROR IN GETTING EVENT'S User DETAILE FOR EVENTID: " + eventId);
                return null;

            }
        } catch (SQLException e1) {
            LOGGER.error("ERROR IN PREPARING STATEMENT FOR EVENT BASED PRICE FOR THE EVENTID : " + eventId);
        }
		// TODO Auto-generated method stub
		return groupByCategotyMap;
	}

	@Override
	public Map<String, List<Fixtures>> makeFixture(Map<String, List<User>> groups) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Map<String, List<Fixtures>> finalFixture = new HashMap<String, List<Fixtures>>();
        for (Map.Entry<String, List<User>> entry : groups.entrySet()) {

            Map<String,List<User>> personByUnderX = new HashMap<>();

            Map<String, Fixtures> fixture = new HashMap<String, Fixtures>();

            personByUnderX = entry.getValue().stream()
                    .collect(Collectors.groupingBy(User::getUnderX));

            List<DrawHelper> dU = processFixture(entry.getKey(), personByUnderX);

            for(DrawHelper d : dU) {
                finalFixture.put(d.getCategotyPlusUnderX(), getFinalFixure(d.getUser(), 1));
            }
        }
		// TODO Auto-generated method stub
		return finalFixture;
	}

    private List<Fixtures> getFinalFixure(List<User> users, int level) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Fixtures> fx = new ArrayList<Fixtures>();
        int totalPlayerInCurrentCategory = users.size();
        String x= EnglishNumberToWords.convert(totalPlayerInCurrentCategory).toUpperCase();
        //// do the hard work later
        ByesContants bc = new ByesContants();
        String numberInString = "get"+x;
        final int[] byeIndexArray = getMethodValue(bc, numberInString);//ByesContants.FIFTYEIGHT;
        int ByesIndexIdentifier=0;

        if(byeIndexArray[0]==-1){
            for(int i=0; i< totalPlayerInCurrentCategory; i=i+2){
                Fixtures f = new Fixtures();
                try {
                    f.setUser1(users.get(i));
                }catch (Exception e){
                    f.setUser1(null);
                }
                try {
                    f.setUser2(users.get(i+1));
                }catch (Exception e){
                    f.setUser2(null);
                }
                fx.add(f);

            }


            return fx;
        }
        int perfectPowerOfTwo = EnglishNumberToWords.nextPowerOf2(totalPlayerInCurrentCategory);

        int userSelectorArrayIndex= 0; ByesIndexIdentifier = 0;

        int k=0;

        while(k<perfectPowerOfTwo){
            Fixtures f = new Fixtures();
            if(ByesIndexIdentifier < byeIndexArray.length && (k==(byeIndexArray[ByesIndexIdentifier])-1)){
                f.setUser1(null);
                k++; ByesIndexIdentifier++;
            }else {
                f.setUser1(users.get(userSelectorArrayIndex++));
                k++;
            }

            if(ByesIndexIdentifier < byeIndexArray.length && (k==(byeIndexArray[ByesIndexIdentifier])-1)){
                f.setUser2(null);
                k++; ByesIndexIdentifier++;
            }else {
                f.setUser2(users.get(userSelectorArrayIndex++));
                k++;
            }
            f.setLevel(level);
            fx.add(f);
        }


        return fx;
    }

    @Override
    public List<DrawHelper> processFixture(String catSex, Map<String, List<User>> personByUnderX) {
        List<DrawHelper> dU = new ArrayList<DrawHelper>();
        for (Map.Entry<String, List<User>> underX : personByUnderX.entrySet()) {
            int totalPlayerInSexWise = underX.getValue().size();
            if( totalPlayerInSexWise > 128) {
                int splitOnIndex = underX.getValue().size() / 2;
                dU.addAll(processFixture(catSex, new HashMap<String, List<User>>(){{
                    put(ByesContants.POOL[counterForPools++] +"-"+ underX.getKey() +"-"+ catSex , underX.getValue().subList(0,splitOnIndex));
                }}));
                dU.addAll(processFixture(catSex, new HashMap<String, List<User>>(){{
                    put(ByesContants.POOL[counterForPools++] +"-"+ underX.getKey() +"-"+ catSex, underX.getValue().subList(splitOnIndex,totalPlayerInSexWise));
                }}));
            }else {
                DrawHelper d = new DrawHelper();
                d.setCategotyPlusUnderX(catSex+underX.getKey());
                d.setUser(underX.getValue());
                dU.add(d);
            }
        }
        return dU;
    }
    public int [] getMethodValue(Object o, String methodName) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = o.getClass();
        String abc = "getFOUR";
        Method fooMethod = clazz.getMethod(methodName);
        Object fooObj = clazz.newInstance();
        int[] bar = (int[]) fooMethod.invoke(fooObj);
        for(Field field : clazz.getDeclaredFields()) {
            //you can also use .toGenericString() instead of .getName(). This will
            //give you the type information as well.

            System.out.println(field.getName());
        }
        return bar;
    }

}
